package GoogleApi;

import Configuration.PropertiesLoader;
import Devices.AM2301;
import Devices.ISensor;
import Devices.SGP30;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static GoogleApi.GoogleCredentials.JSON_FACTORY;

public class GoogleApiService {

    private static final String APPLICATION_NAME = "MiniSmartHome";
    private static NetHttpTransport HTTP_TRANSPORT;

    private Properties conf;
    private Sheets service;

    public GoogleApiService() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        conf = PropertiesLoader.loadProperties();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredentials.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public List<ISensor> getSensorList(String range ) throws IOException {
        String spreadsheetId = conf.getProperty("sheet.id");

        List<List<Object>> values = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
                .getValues();

        List<ISensor> sensors=new ArrayList<>();

        for(List<Object> object : values)
        {
            if(object.get(1).toString().equals("SGP30"))
            {
                sensors.add(new SGP30(Integer.parseInt(object.get(2).toString())));
            }
            if(object.get(1).toString().equals("AM2301"))
            {
                sensors.add(
                        new AM2301(Integer.parseInt(object.get(3).toString()),
                                Integer.parseInt(object.get(4).toString())));
            }
        }

        return sensors;
    }

}
