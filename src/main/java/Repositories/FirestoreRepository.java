package Repositories;

import Configuration.COLLECTIONS;
import Configuration.Config;
import Devices.*;
import Exceptions.DeviceSetupFailedException;
import Exceptions.InvalidCollectionNameException;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class FirestoreRepository<T extends Device> {

    CollectionReference collection;

    List<T> devices = new ArrayList<>();

    public FirestoreRepository(COLLECTIONS collection) throws InvalidCollectionNameException, ExecutionException, InterruptedException {

        Firestore database = FirestoreClient.getFirestore();

        if (collection == COLLECTIONS.SENSORS)
            this.collection = database.collection(Config.SENSORS_COLLECTION_PATH);


        else if (collection == COLLECTIONS.DIMMERS)
            this.collection = database.collection(Config.DIMMERS_COLLECTION_PATH);

        else throw new InvalidCollectionNameException();

        init();
    }

    public void updateData(Sensor device) {

        try {
            var task = collection.document(device.getId()).update("data", device.getData());
            task.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to update data for device: " + device.getName());
            e.printStackTrace();
        }
    }

    public List<T> getDevices() {
        return devices;
    }

    private List<T> requestDevicesFromFirebase(){
        try {
            return collection.get().get().getDocuments().stream().map(this::getDeviceForDocument).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Collection initialization failed.");
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void init() {

        devices = requestDevicesFromFirebase();

        collection.addSnapshotListener((snapshots, e) -> {

            if (e != null) {
                System.err.println("Listen failed: " + e);
                return;
            }

                devices = requestDevicesFromFirebase();
        });
    }

    private T getDeviceForDocument(QueryDocumentSnapshot document) {

        T device;

        String name = document.get("name", String.class);

        switch (name) {
            case "Dimmer":
                device = (T) document.toObject(Dimmer.class);
                break;
            case "SGP30":
                device = (T) document.toObject(SGP30.class);
                break;
            case "AM2301":
                device = (T) document.toObject(AM2301.class);
                break;
            default:
                throw new DeviceSetupFailedException(name);
        }

        device.setId(document.getId());

        return device;

    }

}