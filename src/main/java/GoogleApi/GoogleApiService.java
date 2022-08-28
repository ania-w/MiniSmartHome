package GoogleApi;

import Configuration.PropertiesLoader;
import Devices.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

import static GoogleApi.GoogleCredentials.JSON_FACTORY;

public class GoogleApiService {

    private static final String APPLICATION_NAME = "MiniSmartHome";
    Credential credential;
    NetHttpTransport HTTP_TRANSPORT;
    private final Sheets service;
    String spreadsheetId;
    String dimmerSheetName;
    String sensorSheetName;

    public GoogleApiService() throws GeneralSecurityException, IOException {

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        credential=GoogleCredentials.getCredentials(HTTP_TRANSPORT);
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential )
                .setApplicationName(APPLICATION_NAME)
                .build();

        var conf = PropertiesLoader.loadProperties();
        spreadsheetId = conf.getProperty("sheet.id");
        dimmerSheetName = conf.getProperty("sheet.dimmerSheetName");
        sensorSheetName = conf.getProperty("sheet.sensorSheetName");
    }


    public List<ISensor> getSensorList(boolean powerUpSGP30) throws IOException {

            var values = getSpreadsheetValues(sensorSheetName);

            List<ISensor> sensors = new ArrayList<>();

            for (var object : values) {

                if (object.get(1).toString().equals("SGP30"))
                    sensors.add(new SGP30(objectToInt(object.get(2)), powerUpSGP30));

                else if (object.get(1).toString().equals("AM2301"))
                    sensors.add(new AM2301(objectToInt(object.get(3)), objectToInt(object.get(4))));

                else throw new IllegalStateException("Unexpected value: " + object.get(1).toString());
            }
            return sensors;

    }

    public List<Dimmer> getDimmerList() throws IOException {

            var values = getSpreadsheetValues(dimmerSheetName);

            List<Dimmer> dimmers = new ArrayList<>();

            for (var object : values)
            {
                int desiredBrightness=objectToInt(object.get(3));
                if(desiredBrightness>=0)
                    dimmers.add(new Dimmer(object.get(2).toString(), desiredBrightness));
            }

            return dimmers;
    }

    private Integer objectToInt(Object obj){
        return Integer.parseInt(obj.toString());
    }

    private List<List<Object>> getSpreadsheetValues(String range) throws IOException {
        return service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
                .getValues();
    }


    private List<String> getSensorDataList(List<ISensor> list)  {
        return list.stream()
                .map(ISensor::getData)
                .collect(Collectors.toList());
    }



    public void writeSensorDataToSheet(String columnRange, List<ISensor> sensors) throws IOException {

            var dataList = getSensorDataList(sensors);

            var body = createColumnValueRangeFromList(dataList);

            updateSheet(columnRange, body);

    }

    private void updateSheet(String range, ValueRange body) throws IOException {
        service.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }


    private ValueRange createColumnValueRangeFromList(List<String> list){
        List<List<Object>> values = new ArrayList<>(new ArrayList<>());
        for(var element : list)
            values.add(List.of(element));

        return new ValueRange().setValues(values);
    }

}
