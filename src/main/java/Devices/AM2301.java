/**
 * Based on
 *
 * @see <a href=https://www.uugear.com/portfolio/dht11-humidity-temperature-sensor-module>uugear.com</a>
 * <p>
 * Created using wiringPi
 * @see <a href=http://wiringpi.com>wiringPi</a>
 * <p>
 * Should be suitable for DHT22, DHT11
 */

package Devices;

import Exceptions.DeviceSetupFailedException;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

import javax.annotation.PostConstruct;

import static com.pi4j.wiringpi.Gpio.delayMicroseconds;

import java.util.*;


/**
 * Temperature [C] & Humidity sensor [%]
 * AM2301 = DHT21
 */

public class AM2301 extends Sensor {

    private Integer pin;
    private Integer boardpin;

    public Long getPin() {
        return Long.valueOf(pin);
    }

    public void setPin(Long pin) {
        this.pin = Math.toIntExact(pin);
    }

    public Long getBoardpin() {
        return Long.valueOf(boardpin);
    }

    public void setBoardpin(Long boardpin) {
        this.boardpin = Math.toIntExact(boardpin);
        GpioUtil.export(Math.toIntExact(boardpin), GpioUtil.DIRECTION_OUT);
    }

    public AM2301() {
        super("AM2301");
        if (Gpio.wiringPiSetup() == -1)
            throw new DeviceSetupFailedException(this.getClass().getSimpleName());
    }


    @Override
    public void read() {

        sendMeasurementRequest();
        getSensorResponse().ifPresent(r -> data = r);

    }


    private void sendMeasurementRequest() {
        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        delayMicroseconds(900); // At least 800us

        Gpio.digitalWrite(pin, Gpio.HIGH);
        delayMicroseconds(30); // 20-40 ms
        Gpio.pinMode(pin, Gpio.INPUT);
    }

    private Optional<Map<String, Double>> getSensorResponse() {
        var lastState = Gpio.HIGH;
        var bits = 0;
        var raw_data = new Integer[5];
        Arrays.fill(raw_data, 0);
        for (int i = 0; i <= 84; i++) {

            var counter = 0;

            while (Gpio.digitalRead(pin) == lastState) {
                counter++;
                delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            if (i >= 3 && i % 2 == 0 && bits < 40) {
                raw_data[bits / 8] <<= 1;
                if (counter > 16) {
                    raw_data[bits / 8] |= 1;
                }
                bits++;
            }

            if (bits == 40)
                break;

            lastState = Gpio.digitalRead(pin);
        }


        return getFormattedData(raw_data);
    }

    private Optional<Map<String, Double>> getFormattedData(Integer[] raw_data) {
        Map<String, Double> formattedData;

        if (checkParity(raw_data)) {
            formattedData = new HashMap<>();
            formattedData.put("humidity", (raw_data[0] * 256 + raw_data[1]) * 0.1d);
            if ((raw_data[2] & 0x80) != 0)  // negative temp
                formattedData.put("temperature", -1 * (raw_data[2] * 256 + raw_data[3]) * 0.1d);
            else
                formattedData.put("temperature", (raw_data[2] * 256 + raw_data[3]) * 0.1d);
        } else
            return Optional.empty();

        return Optional.of(formattedData);
    }

    private boolean checkParity(Integer[] raw_data) {
        return (((raw_data[0] + raw_data[1] + raw_data[2] + raw_data[3]) & 0x00FF) == raw_data[4]);
    }

}