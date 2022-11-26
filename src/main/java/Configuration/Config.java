package Configuration;


public class Config {

    public static final String SENSORS_COLLECTION_PATH = PropertiesLoader.getProperty("sensor-collection-name");
    public static final String DIMMERS_COLLECTION_PATH = PropertiesLoader.getProperty("dimmer-collection-name");
    public static final String PROJECT_ID=PropertiesLoader.getProperty("project-id");
    public static final String DATABASE_URL=PropertiesLoader.getProperty("database-url");
    public static final String CREDENTIALS = PropertiesLoader.getProperty("credentials-file-path") ;
    public static final String ROOMS_COLLECTION_PATH = PropertiesLoader.getProperty("rooms-collection-name");
    public static final String DIMMER_DATA_COLLECTION_PATH = PropertiesLoader.getProperty("dimmer-data-collection-name");
    public static final String SENSOR_DATA_COLLECTION_PATH = PropertiesLoader.getProperty("sensor-data-collection-name");

}
