import Devices.Dimmer;
import GoogleApi.GoogleApiService;
import Devices.ISensor;

import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.List;


public class App {


    public static void main(String... args) throws IOException, GeneralSecurityException, InterruptedException {
        GoogleApiService service=new GoogleApiService();
        Dimmer dimmer=new Dimmer("192.168.1.32");
        List<ISensor> sensors=service.getSensorList("A2:E6");
        System.out.println(dimmer.setLightIntensity(50));
        while(true)
        {
            for (ISensor sensor: sensors) {
                System.out.println(sensor.read());
            }

            Thread.sleep(2000);
        }


    }
}
