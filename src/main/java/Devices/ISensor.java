package Devices;

import com.google.gson.Gson;

import java.io.IOException;

public interface ISensor {
    void read() throws IOException, InterruptedException;
    String getData();
    int ERROR_CODE = 101;
}
