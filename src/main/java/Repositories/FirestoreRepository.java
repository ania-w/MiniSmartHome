package Repositories;

import Configuration.Config;
import Models.*;
import Exceptions.DeviceSetupFailedException;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class FirestoreRepository {

    CollectionReference roomsCollection;
    CollectionReference roomDimmerDataCollection;
    CollectionReference roomSensorDataCollection;
    CollectionReference sensorsCollection;
    CollectionReference dimmersCollection;

    List<Room> rooms = new ArrayList<>();
    List<Sensor> sensors = new ArrayList<>();
    List<Dimmer> dimmers = new ArrayList<>();

    public FirestoreRepository() {
        Firestore database = FirestoreClient.getFirestore();

        this.roomsCollection = database.collection(Config.ROOMS_COLLECTION_PATH);
        this.roomDimmerDataCollection = database.collection(Config.DIMMER_DATA_COLLECTION_PATH);
        this.roomSensorDataCollection = database.collection(Config.SENSOR_DATA_COLLECTION_PATH);
        this.sensorsCollection = database.collection(Config.SENSORS_COLLECTION_PATH);
        this.dimmersCollection = database.collection(Config.DIMMERS_COLLECTION_PATH);

        createRoomsLists();
        createDimmerCollection();
        createSensorCollection();
    }

    private void createSensorCollection() {
        try {
            sensors = sensorsCollection.get().get().getDocuments().stream().map(this::getSensorForDocument).collect(Collectors.toList());
            setOnSensorUpdateListener();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("DimmerData collection initialization failed.");
            e.printStackTrace();
        }
    }

    private void setOnSensorUpdateListener() {
        sensorsCollection.addSnapshotListener((snapshots, e) -> {

            if (isErr(snapshots, e)) return;

            for (var dc : snapshots.getDocumentChanges()) {
                var updatedObj = dc.getDocument().toObject(Sensor.class);
                var id = dc.getDocument().getId();
                switch (dc.getType()) {
                    case MODIFIED:
                        sensors = sensors.stream().map(s -> s.getId().equals(id) ? updatedObj : s).collect(Collectors.toList());
                    case ADDED:
                        if (sensors.stream().noneMatch(s -> s.getId().equals(id)))
                            sensors.add(updatedObj);
                        break;
                    case REMOVED:
                        var toBeRemoved = sensors.stream().filter(r -> r.getId().equals(id)).findFirst().get();
                        sensors.remove(toBeRemoved);
                }
            }
        });
    }

    private void createDimmerCollection() {
        try {
            dimmers = dimmersCollection.get().get().toObjects(Dimmer.class);
            setOnDimmerUpdateListener();
            setOnDimmerDataUpdateListener();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("DimmerData collection initialization failed.");
            e.printStackTrace();
        }
    }

    private void setOnDimmerUpdateListener() {

        dimmersCollection.addSnapshotListener((snapshots, e) -> {

            if (isErr(snapshots, e)) return;

            for (var dc : snapshots.getDocumentChanges()) {
                var updatedObj = dc.getDocument().toObject(Dimmer.class);
                var id = dc.getDocument().getId();
                switch (dc.getType()) {
                    case MODIFIED:
                        dimmers = dimmers.stream().map(d -> d.getId().equals(id) ? updatedObj : d).collect(Collectors.toList());
                        break;
                    case ADDED:
                        if (dimmers.stream().noneMatch(r -> r.getId().equals(id)))
                            dimmers.add(updatedObj);
                        break;
                    case REMOVED:
                        var toBeRemoved = dimmers.stream().filter(r -> r.getId().equals(id)).findFirst().get();
                        dimmers.remove(toBeRemoved);
                }
            }
        });
    }


    private void setOnDimmerDataUpdateListener() {
        roomDimmerDataCollection.addSnapshotListener((snapshots, e) -> {

            if (isErr(snapshots, e)) return;

            for (var dc : snapshots.getDocumentChanges()) {
                var updatedObj = dc.getDocument().toObject(DimmerDTO.class).getLight_intensity_map();
                for (var id : updatedObj.keySet())
                    dimmers.stream().filter(dimmer -> dimmer.getId().equals(id)).findFirst().get().setLightIntensity(updatedObj.get(id));
            }
        });
    }

    public void createRoomsLists() {
        try {
            rooms = roomsCollection.get().get().toObjects(Room.class);
            setOnRoomsUpdateListener();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Rooms collection initialization failed.");
            e.printStackTrace();
        }
    }


    private void setOnRoomsUpdateListener() {

        roomsCollection.addSnapshotListener((snapshots, e) -> {

            if (isErr(snapshots, e)) return;

            for (var dc : snapshots.getDocumentChanges()) {
                var updatedRoom = dc.getDocument().toObject(Room.class);
                var id = dc.getDocument().getId();
                switch (dc.getType()) {
                    case MODIFIED:
                        rooms = rooms.stream().map(r -> r.getId().equals(id) ? updatedRoom : r).collect(Collectors.toList());
                        break;
                    case ADDED:
                        if (rooms.stream().noneMatch(r -> r.getId().equals(id)))
                            rooms.add(updatedRoom);
                        break;
                    case REMOVED:
                        var toBeRemoved = rooms.stream().filter(r -> r.getId().equals(id)).findFirst().get();
                        rooms.remove(toBeRemoved);
                }
            }
        });
    }


    public boolean isErr(QuerySnapshot snapshots, FirestoreException e) {
        if (e != null || snapshots == null) {
            System.err.println("Listen failed: " + e);
            return true;
        }
        return false;
    }


    public void updateSensorData(String id, Map<String, Double> data) {
        try {
            var update = (Map<String, Double>) Objects.requireNonNull(roomSensorDataCollection.document(id).get().get().getData()).get("data");
            update.putAll(data);
            var task = roomSensorDataCollection.document(id).update("data", update);
            task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    private Sensor getSensorForDocument(QueryDocumentSnapshot document) {

        Sensor sensor;

        String name = document.get("class_name", String.class);

        switch (name) {
            case "SGP30":
                sensor = document.toObject(SGP30.class);
                break;
            case "AM2301":
                sensor = document.toObject(AM2301.class);
                break;
            default:
                throw new DeviceSetupFailedException(name);
        }

        return sensor;
    }

}
