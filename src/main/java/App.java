import Devices.Dimmer;
import Devices.ISensor;
import GoogleApi.GoogleApiService;
import Threads.DimmerThread;
import Threads.SensorThread;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;


public class App  {

    public static void main(String... args){

        Runnable dimmerRunnable=new DimmerThread();
        Thread dimmerThread=new Thread(dimmerRunnable);
        dimmerThread.start();

        Runnable sensorRunnable=new SensorThread();
        Thread sensorThread=new Thread(sensorRunnable);
        sensorThread.start();
    }
}
