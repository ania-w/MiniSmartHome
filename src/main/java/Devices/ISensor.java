package Devices;

import java.io.IOException;

public interface ISensor {
    void read() throws IOException, InterruptedException;
    String getData();
}
