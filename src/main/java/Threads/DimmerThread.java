package Threads;

import Devices.Dimmer;
import GoogleApi.GoogleApiService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 *  Thread for updating dimmer light intensity basing on google sheets data
 */
public class DimmerThread {

    GoogleApiService service = new GoogleApiService();

    public DimmerThread() throws GeneralSecurityException, IOException {
    }

    public void run(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                List<Dimmer> dimmers = service.getDimmerList();
                for (Dimmer dimmer : dimmers) dimmer.setLightIntensity();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 550, TimeUnit.MILLISECONDS);
    }

}