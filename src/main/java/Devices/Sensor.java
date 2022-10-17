package Devices;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public abstract class Sensor extends Device {

    protected Map<String, Double> data = new HashMap<>();

    public Sensor(String name) {
        super(name);
    }

    public final Map<String, Double> getData() {
        return data;
    }

    public abstract void read();

}
