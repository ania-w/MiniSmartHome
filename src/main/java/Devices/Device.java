package Devices;

import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public abstract class Device {
    String id;
    private String name;

    public Device(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
