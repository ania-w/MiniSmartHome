package Threads;
import Devices.ISensor;
import GoogleApi.GoogleApiService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *  Thread for updating sensor data in google sheets
 */
public class SensorThread {

    GoogleApiService service = new GoogleApiService();

    public SensorThread() throws GeneralSecurityException, IOException {
    }

    public void run(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                List<ISensor> sensors=service.getSensorList();
                for (var sensor : sensors) sensor.read();
                service.writeSensorData("F2:F" + (sensors.size() + 1), sensors);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

}