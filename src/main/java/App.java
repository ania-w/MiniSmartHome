import Sensors.AM2301;

public class App {

    public static void main(String args[]) throws InterruptedException {

        final AM2301 am2301 = new AM2301(7,4);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(2000);
            System.out.println(am2301.read());
        }


    }

}
