package Threads;

import Devices.ISensor;
import GoogleApi.GoogleApiService;

import java.util.List;

public class SensorThread implements Runnable {
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
                List<ISensor> sensors=service.getDevicesList("Sensors!A2:E");
                // read sensor data
                for (ISensor sensor : sensors) {
                    sensor.read();
                }
                // write sensor data to google sheets
                service.writeSensorData("F2:F" + (sensors.size() + 1), sensors);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}