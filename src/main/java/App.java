import Threads.DimmerThread;
import Threads.SensorThread;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class App  {

    public static void main(String... args) throws GeneralSecurityException, IOException {

        var dimmerThread=new DimmerThread();
       dimmerThread.run();

        var sensorThread=new SensorThread();
        sensorThread.run();

    }
}
