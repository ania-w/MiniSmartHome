/**
 *  Based on the package io.helins.linux.i2c created by helins.linux.i2c
 *  @see <a href=https://github.com/helins/linux-i2c.java>helins/linux-i2c</a>
 */

package Devices;

import io.helins.linux.i2c.I2CBuffer;
import io.helins.linux.i2c.I2CBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *  SGP30 Co2 & Tvoc sensor
 *
 *  Measurment range:
 *  Tvoc: 0   - 60 000 ppm
 *  Co2:  400 - 60 000 ppb
 */
public class SGP30 implements ISensor
{
    private final static int ADDRESS=0x58;
    private final static int START_REG=0x20;
    private final static int INIT=0x03;
    private final static int MEASURE=0x08;
    private final static int GET_BASELINE=0x15;
    private final static int SET_BASELINE=0x1e;
    //TODO set humidity
    private final static int SET_HUMIDITY=0x61;

    private final static int GET_FEATURE_SET_VERSION=0x2f;
    private final static int MEASURE_RAW_SIGNALS=0x50;

    private final static int SGP30_CRC8_POLYNOMIAL=0x31;
    private final static int SGP30_CRC8_INIT=0xFF;

    private final I2CBus bus;
    String data;

    public String getData() {
        return data;
    }


    public SGP30(int bus,boolean powerUp) throws IOException {
        this.bus=new I2CBus(bus);
        this.bus.selectSlave(ADDRESS);
        if(powerUp) powerUp();
    }


    @Override
    public void read() throws IOException, InterruptedException {

        sendMeasureRequest();

        readOutput();
    }

    private void readOutput() throws IOException {
        I2CBuffer buffer=readReg();
        var co2=buffer.get(0)<<8 | buffer.get(1);
        var tvoc=buffer.get(3)<<8 | buffer.get(4);

        if(CRC(co2)!=buffer.get(2) || CRC(tvoc)!=buffer.get(5))
        {
            co2=ERROR_CODE;
            tvoc=ERROR_CODE;
        }

        data = "{\"co2\":"+co2+","
                +"\"tvoc\":"+tvoc+"}";
    }

    private void sendMeasureRequest() throws IOException, InterruptedException {
        writeReg(START_REG,MEASURE);
        TimeUnit.MILLISECONDS.sleep(60);
    }

    public void powerUp() throws IOException {
        writeReg(START_REG,INIT);
    }

    private void writeReg(int register, int command) throws IOException {

        bus.selectSlave(ADDRESS);

        I2CBuffer buffer = new I2CBuffer(3)
                .set(0,register)
                .set( 1, command)
                .set( 2, SGP30.CRC(command));

        bus.write(buffer);
    }

    private I2CBuffer readReg() throws IOException {
        I2CBuffer buffer = new I2CBuffer(6);
        bus.read(buffer) ;

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
