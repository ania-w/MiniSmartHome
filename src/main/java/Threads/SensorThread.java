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
    List<ISensor> sensors=service.getSensorList(true);

    public SensorThread() throws GeneralSecurityException, IOException {
    }

    public void run(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                sensors=service.getSensorList(false);
                for (var sensor : sensors) sensor.read();
                service.writeSensorDataToSheet("F2:F" + (sensors.size() + 1), sensors);
            } catch (IOException | InterruptedException ignored) {
            }

        }, 0, 3000, TimeUnit.MILLISECONDS);
    }

}