package Threads;

import Models.Sensor;
import Services.FirestoreService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainThread {

    FirestoreService service = new FirestoreService();

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public MainThread() {
    }

    public void run() {
        System.out.println("Starting application..");
        readSensorDataLoop();
        System.out.println("Application started on " + LocalDate.now());
    }

  void readSensorDataLoop() {

        executor.scheduleAtFixedRate(() -> {
            try {
                List<Sensor> sensorList = service.getSensors();
                for (var sensor : sensorList) {
                    sensor.read();
                }
                service.updateSensorData(sensorList);
            } catch (Exception e){
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

}
