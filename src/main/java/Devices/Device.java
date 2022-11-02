package Devices;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public abstract class Device {
    String id;
    private String name;

    private String room;

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

    protected Map<String, Double> data = new HashMap<>();
    public Map<String, Double> getData() {
        return data;
    }

    public String getRoom() { return room; }

    public void setRoom(String room) { this.room = room; }
}
