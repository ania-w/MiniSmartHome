/**
 * Based on the package io.helins.linux.i2c created by helins.linux.i2c
 *
 * @see <a href=https://github.com/helins/linux-i2c.java>helins/linux-i2c</a>
 */

package Devices;

import Exceptions.DeviceSetupFailedException;
import Exceptions.FailedReadingDataException;
import com.google.firebase.database.Exclude;
import io.helins.linux.i2c.I2CBuffer;
import io.helins.linux.i2c.I2CBus;
import lombok.NoArgsConstructor;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *  SGP30 Co2 & Tvoc sensor
 *
 *  Measurment range:
 *  Tvoc: 0   - 60 000 ppm
 *  Co2:  400 - 60 000 ppb
 */
public class SGP30 extends Sensor {
    private final static int ADDRESS = 0x58;
    private final static int START_REG = 0x20;
    private final static int INIT = 0x03;
    private final static int MEASURE = 0x08;

    //TODO:
    private final static int GET_BASELINE = 0x15;
    private final static int SET_BASELINE = 0x1e;
    private final static int SET_HUMIDITY = 0x61;

    private final static int GET_FEATURE_SET_VERSION = 0x2f;
    private final static int MEASURE_RAW_SIGNALS = 0x50;

    private final static int SGP30_CRC8_POLYNOMIAL = 0x31;
    private final static int SGP30_CRC8_INIT = 0xFF;

    private static I2CBus bus;

    private Integer busNumber;

    public Long getBusNumber() {
        return Long.valueOf(busNumber);
    }

    public void setBusNumber(Long busNumber) {
        this.busNumber = Math.toIntExact(busNumber);
        init();
    }

    public SGP30() {
        super("SGP30");
    }


    private void init() {
        try {
            bus = new I2CBus(busNumber);
            bus.selectSlave(ADDRESS);
            powerUp();
        } catch (IOException e) {
            throw new DeviceSetupFailedException(this.getClass().getSimpleName());
        }
    }

    @Override
    public void read() {

        sendMeasurementRequest();

        data = readOutput();

    }

    private Map<String, Double> readOutput() {
        I2CBuffer buffer = readReg();
        var co2 = buffer.get(0) << 8 | buffer.get(1);
        var tvoc = buffer.get(3) << 8 | buffer.get(4);

        if (CRC(co2) != buffer.get(2) || CRC(tvoc) != buffer.get(5))
            throw new FailedReadingDataException("Invalid CRC.");

        Map<String, Double> respose = new HashMap<>();
        respose.put("co2", (double) co2);
        respose.put("tvoc", (double) tvoc);

        return respose;
    }

    private void sendMeasurementRequest() {
        writeReg(START_REG, MEASURE);
        try {
            TimeUnit.MILLISECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void powerUp() throws IOException {
        writeReg(START_REG, INIT);
    }

    private void writeReg(int register, int command) {

        try {
            bus.selectSlave(ADDRESS);

            I2CBuffer buffer = new I2CBuffer(3)
                    .set(0, register)
                    .set(1, command)
                    .set(2, SGP30.CRC(command));

            bus.write(buffer);
        } catch (IOException e) {
            throw new FailedReadingDataException("Failed to send command " + command);
        }

    }

    private I2CBuffer readReg() {

        I2CBuffer buffer;

        try {
            buffer = new I2CBuffer(6);
            bus.read(buffer);
        } catch (IOException e) {
            throw new FailedReadingDataException("Failed to read buffer");
        }

        return buffer;
    }


    private static int CRC(int data) {
        var crc = 0xFF;
        for (var _byte : new int[]{(data & 0xff00) >> 8, data & 0x00ff}) {
            crc ^= _byte;
            for (int i = 0; i < 8; i++) {
                int test = crc & 0x80;
                if (test != 0) {
                    crc = (crc << 1) ^ 0x31;
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc & 0xFF;
    }


}
