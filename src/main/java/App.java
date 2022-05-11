import Sensors.AM2301;
import Sensors.ISensor;
import Sensors.SGP30;
import io.helins.linux.i2c.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String args[]) throws InterruptedException, IOException {

        ISensor<Integer> sgp30=new SGP30(1);
        ISensor<Float> am2301=new AM2301(7,4);

        for (int i = 0; i < 100; i++) {

            System.out.println(am2301.read());
            System.out.println(sgp30.read());
            Thread.sleep(1000);
        }


    }

}
