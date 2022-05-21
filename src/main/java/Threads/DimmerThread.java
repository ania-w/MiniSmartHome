package Threads;

import Devices.Dimmer;
import GoogleApi.GoogleApiService;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DimmerThread implements Runnable {
    public void run()
    {
        // service for communication with google sheets
        GoogleApiService service = null;
        try {
            service=new GoogleApiService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true)
        {
            try {
                List<Dimmer> dimmers=service.getDevicesList("Dimmers!A2:D");
                // set light intensity for each dimmer based on desired brightness from google sheets
                for (int i=0; i<dimmers.size();i++)
                    dimmers.get(i).setLightIntensity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}