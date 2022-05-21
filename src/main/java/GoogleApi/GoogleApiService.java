package GoogleApi;

import Configuration.PropertiesLoader;
import Devices.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

import static GoogleApi.GoogleCredentials.JSON_FACTORY;

public class GoogleApiService {

    private static final String APPLICATION_NAME = "MiniSmartHome";
    private static NetHttpTransport HTTP_TRANSPORT;

    private final Sheets service;
    String spreadsheetId;

    /**
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public GoogleApiService() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredentials.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Read sheet id from application.properties
        Properties conf = PropertiesLoader.loadProperties();
        spreadsheetId = conf.getProperty("sheet.id");
    }


    /**
     *
     * @param range Eg. "Sensors!A1:B2"
     * @param <T>   Device class implementing IDevice interface
     * @return      List of devices in the specified sheet
     * @throws IOException
     * TODO: cast to T looks ugly :(
     */
    public <T> List<T> getDevicesList(String range) throws IOException{

        List<List<Object>> values = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
                .getValues();

        List<T> devices=new ArrayList<>();

        for(List<Object> object : values)
        {
            switch (object.get(1).toString())
            {
                case "SGP30": {
                    devices.add((T) new SGP30(Integer.parseInt(object.get(2).toString())));
                    break;
                }
                case "AM2301": {
                    devices.add((T) new AM2301(Integer.parseInt(object.get(3).toString()),
                                    Integer.parseInt(object.get(4).toString())));
                    break;
                }
                case "Dimmer":{
                    devices.add((T) new Dimmer(object.get(2).toString(),
                            Integer.parseInt(object.get(3).toString())));
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + object.get(1).toString());
            }
        }

        return devices;
    }

    /**
     * Read data as list of String values, instead of default google's List<List<Object>>
     * @param range Eg. "Sensors!A1:B2"
     * @return  List of String values
     * @throws IOException
     */
    public List<String> readDataAsList(String range) throws IOException {

        List<List<Object>> values = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
                .getValues();

        return  values.stream()
                .flatMap(List::stream)
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
    }


    /**
     *  Write data from getData() to specified range
     * @param range Eg. "Sensors!A1:B2"
     * @param sensors list of sensors
     * @return
     * @throws IOException
     */
    public Integer writeSensorData(String range, List<ISensor> sensors) throws IOException {

        List<String> dataList=sensors.stream()
                .map(sensor-> sensor.getData())
                .collect(Collectors.toList());
        List<List<Object>> values = new ArrayList<>(new ArrayList<>());

        for(String data : dataList)
        {
               values.add(Arrays.asList(data));
        }

        ValueRange body = new ValueRange()
                .setValues(values);
        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption("RAW")
                        .execute();
        return result.getUpdatedCells();
    }

}
