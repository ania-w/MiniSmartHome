package Models;

import java.util.Map;
import java.util.Optional;


public abstract class Sensor extends Device {
    public abstract Optional<Map<String,Double>> read();
}
