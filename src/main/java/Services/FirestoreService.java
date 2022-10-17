package Services;

import Configuration.COLLECTIONS;
import Configuration.Config;
import Devices.Device;
import Devices.Sensor;
import Exceptions.InvalidCollectionNameException;
import Repositories.FirestoreRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class FirestoreService<T extends Device> {

    FirestoreRepository<T> repository;

    public FirestoreService(COLLECTIONS collection) throws InvalidCollectionNameException {

        try {

            InputStream serviceAccount = getClass().getResourceAsStream(Config.CREDENTIALS);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(Config.PROJECT_ID)
                    .setDatabaseUrl(Config.DATABASE_URL)
                    .build();

            if (FirebaseApp.getApps().isEmpty())
                FirebaseApp.initializeApp(options);

            repository = new FirestoreRepository<>(collection);

        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            System.exit(2);
            System.err.println("Failed to initialise repository");
        }

    }

    public List<T> getAll() {
        return repository.getDevices();
    }

    public void update(Sensor sensor) {
        repository.updateData(sensor);
    }

}
