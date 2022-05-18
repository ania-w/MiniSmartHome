package Devices;

import java.io.IOException;
import java.util.Map;

public interface ISensor<T> {
    Map<String,T> read() throws IOException, InterruptedException;
}
