package Threads;

import Configuration.COLLECTIONS;
import Devices.Dimmer;

import Devices.Sensor;
import Exceptions.InvalidCollectionNameException;
import Services.FirestoreService;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainThread {

    FirestoreService<Sensor> sensorService = new FirestoreService<>(COLLECTIONS.SENSORS);
    FirestoreService<Dimmer> dimmerService = new FirestoreService<>(COLLECTIONS.DIMMERS);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public MainThread() throws InvalidCollectionNameException {
    }

    public void run() {
        System.out.println("Starting application..");
        readSensorDataLoop(); //17,2k writes per day
        setLightIntensityLoop();
        System.out.println("Application started on "+ LocalDate.now());
    }

  void readSensorDataLoop() {

        executor.scheduleAtFixedRate(() -> {
            for (var sensor : sensorService.getAll()) {
                var data = sensor.getData();
                sensor.read();
                if (sensor.getData().equals(data))
                    sensorService.update(sensor);
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    void setLightIntensityLoop() {

        executor.scheduleAtFixedRate(() -> {
                for (var dimmer : dimmerService.getAll()) {
                    dimmer.setLightIntensity();
                }
        }, 0, 1, TimeUnit.SECONDS);

    }

}
