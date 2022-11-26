package Services;

import Configuration.Config;
import Models.Sensor;
import Repositories.FirestoreRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class FirestoreService {

    FirestoreRepository repository;

    public FirestoreService() {

        try {

            InputStream serviceAccount = getClass().getResourceAsStream(Config.CREDENTIALS);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(Config.PROJECT_ID)
                    .setDatabaseUrl(Config.DATABASE_URL)
                    .build();

            if (FirebaseApp.getApps().isEmpty())
                FirebaseApp.initializeApp(options);

            repository = new FirestoreRepository();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
            System.err.println("Failed to initialise repository");
        }

    }

    public void updateSensorData(List<Sensor> sensors) {
        repository.updateSensorData(sensors);
    }

    public List<Sensor> getSensors() {
        return repository.getSensors();
    }

}
