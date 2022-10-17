package Exceptions;

public class DeviceSetupFailedException extends RuntimeException {
    public DeviceSetupFailedException(String deviceName) {
        super("Setup for " + deviceName + " has failed");
    }
}
