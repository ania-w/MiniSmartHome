package Models;

import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public class DimmerDTO {
    private String id;
    private Map<String,Integer> light_intensity_map;
    private String room_id;

    public Map<String, Integer> getLight_intensity_map() {
        return light_intensity_map;
    }

    public void setLight_intensity_map(Map<String, Integer> light_intensity_map) {
        this.light_intensity_map = light_intensity_map;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
