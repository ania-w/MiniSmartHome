/**
 *   Based on
 *   @see <a href=https://www.uugear.com/portfolio/dht11-humidity-temperature-sensor-module>uugear.com</a>
 *
 *   Created using wiringPi
 *   @see <a href=http://wiringpi.com>wiringPi</a>
 *
 *   Should be suitable for DHT22, DHT11
 */

package Devices;


import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

import static com.pi4j.wiringpi.Gpio.delayMicroseconds;
import static java.lang.Float.NaN;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Temperature [C] & Humidity sensor [%]
 * AM2301 = DHT21
 */
public class AM2301 implements ISensor{

    Integer pin;
    String data;

    /**
     * @return  data in string format {temperature=x, humidity=x}
     */
    public String getData() {
        return data;
    }

    /**
     * @param board_pin     pin number on the board
     * @param pi4j_pin      pi4j GPIO number, check their webpage
     */
    public AM2301(Integer board_pin,Integer pi4j_pin)  {

        if (Gpio.wiringPiSetup() == -1)
            throw new RuntimeException("Gpio.wiringPiSetup() has failed.");

        this.pin=pi4j_pin;

        GpioUtil.export(board_pin, GpioUtil.DIRECTION_OUT);
    }

    /**
     * Reads data from sensor
     */
    @Override
    public void read() {

        startTransmission();

        data=readSensorResponse();

        endTransmission();
    }

    /**
     *  MCU sends start transmission request to sensor
     */
    private void startTransmission()
    {
        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        delayMicroseconds(900); // At least 800us

        Gpio.digitalWrite(pin, Gpio.HIGH);
        delayMicroseconds(30); // 20-40 ms
        Gpio.pinMode(pin, Gpio.INPUT);
    }

    /**
     * @return String {"temperature", "humidity"}
     */
    private String readSensorResponse()
    {
        var lastState = Gpio.HIGH;
        var bits = 0;
        var raw_data=new Integer[5];
        Arrays.fill(raw_data,0);
        for (int i = 0; i <= 84; i++) {

            var counter = 0;

            while (Gpio.digitalRead(pin) == lastState) {
                counter++;
                delayMicroseconds(1);
                if (counter ==255) {
                    break;
                }
            }

            if (i>=3 && i % 2 == 0 && bits<40) {
                raw_data[bits / 8] <<= 1;
                if (counter > 16) {
                    raw_data[bits / 8] |= 1;
                }
                    bits++;
            }

            if(bits==40)
                break;

            lastState = Gpio.digitalRead(pin);

        }

        float temperature=NaN;
        float humidity=NaN;
        if (checkParity(raw_data)) {
            humidity = (raw_data[0] *256 + raw_data[1]) *0.1f;
            temperature = (raw_data[2]*256 + raw_data[3]) * 0.1f;
            if ((raw_data[2] & 0x80)!=0)  // negative temp
                temperature*= -1;
        }

        return Stream.of(new Object[][] {
                        { "temperature", temperature},
                        { "humidity", humidity },
                }).collect(Collectors.toMap(x -> (String) x[0], x -> (Float)x[1]))
                .toString();
    }

    private boolean checkParity(Integer[] raw_data)
    {
        return (((raw_data[0] + raw_data[1] + raw_data[2] + raw_data[3]) & 0x00FF)==raw_data[4]);
    }

    private void endTransmission(){
        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.HIGH);
    }

}