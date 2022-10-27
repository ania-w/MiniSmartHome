package Devices;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public abstract class Sensor extends Device {


    public Sensor(String name) {
        super(name);
    }


    public abstract void read();

}
