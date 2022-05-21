package Devices;

import java.io.IOException;
import java.util.Map;

public interface ISensor {
    void read() throws IOException, InterruptedException;
    String getData();
}
