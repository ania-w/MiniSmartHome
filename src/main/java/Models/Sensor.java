package Models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public abstract class Sensor extends Device {

    protected transient Map<String,Double> data=new HashMap<>();

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public abstract void read();
}
