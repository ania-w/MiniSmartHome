package Models;

import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public class SensorData {

    private String id;

    Map<String,Double> data;

    private String room_id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }



}
