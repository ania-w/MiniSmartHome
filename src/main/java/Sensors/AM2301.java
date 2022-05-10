package Sensors;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import static com.pi4j.wiringpi.Gpio.delayMicroseconds;

import java.util.*;

/*
 *  AM2301 = DHT21
 */
public class AM2301 {
    Integer pin;

    public AM2301(Integer pi4j_pin, Integer board_pin)  {

        if (Gpio.wiringPiSetup() == -1)
            throw new RuntimeException("Gpio.wiringPiSetup() has failed.");

        this.pin=pi4j_pin; // wiringPi GPIO number, check their webpage

        GpioUtil.export(board_pin, GpioUtil.DIRECTION_OUT); // Pin number on the board - not wiringPi!
    }

    public HashMap<String,Float> read() {

        /*
         * Step 1: MCU sends out starting signal to AM2301
         */

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delayMicroseconds(900); // At least 800us

        Gpio.digitalWrite(pin, Gpio.HIGH);
        delayMicroseconds(30); // 20-40 ms
        Gpio.pinMode(pin, Gpio.INPUT);

        /*
         * Step 2: AM2301 starts data transmission to MCU
         */

        int lastState = Gpio.HIGH;
        int bits = 0;
        Integer[] raw_data=new Integer[5];
        HashMap<String,Float> data=new HashMap<>(2);

        for (int i = 0; i <= 84; i++) {

            int counter = 0;

            while (Gpio.digitalRead(pin) == lastState) {
                counter++;
                delayMicroseconds(1);
                if (counter ==255) {
                    break;
                }
            }

            // ignore first 3 transitions
            if (i>=4 && i % 2 == 0 && bits<raw_data.length * 8) {
                raw_data[bits / 8] <<= 1;
                if (counter > 16) {
                    raw_data[bits / 8] |= 1;
                }
                if(bits>40)
                    break;
                else
                    bits++;
            }

            lastState = Gpio.digitalRead(pin);

        }

        /*
         * Step 3: Check parity and perform calculations
         */

        int sum=raw_data[0] + raw_data[1] + raw_data[2] + raw_data[3] & 0x00FF;
        if (raw_data[4] == sum) {
            float h = (raw_data[0] *256 + raw_data[1]) *0.1f;
            float t = (raw_data[2]*256 + raw_data[3]) * 0.1f;
            if ((raw_data[2] & 0x80)!=0)  // negative temp
                t*= -1;
            data.put("temperature",t);
            data.put("humidity",h);
        }

        /*
         * Step 4: End of the transmission
         */

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.HIGH);

        return data;
    }


}