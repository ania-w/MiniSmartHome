package Threads;

import Services.FirestoreService;

import java.time.LocalDate;
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
        readSensorDataLoop(); //17,2k writes per day
        System.out.println("Application started on " + LocalDate.now());
    }

  void readSensorDataLoop() {

        executor.scheduleAtFixedRate(() -> {
            Optional<Map<String,Double>> data;
            for (var sensor : service.getSensors()) {
                data = sensor.read();
                data.ifPresent(map -> service.updateSensorData(sensor.getData_destination_id(), map));
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

}
