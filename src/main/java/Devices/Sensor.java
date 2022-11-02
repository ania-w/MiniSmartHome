package Devices;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public abstract class Sensor extends Device {

    public Sensor(String name) {
        super(name);
    }

    public abstract void read();

}
