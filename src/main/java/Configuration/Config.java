package Configuration;


import java.util.Properties;

public class Config {

    public static final String SENSORS_COLLECTION_PATH = PropertiesLoader.getProperty("sensor-collection-name");
    public static final String DIMMERS_COLLECTION_PATH = PropertiesLoader.getProperty("dimmer-collection-name");
    public static final String PROJECT_ID=PropertiesLoader.getProperty("project-id");
    public static final String DATABASE_URL=PropertiesLoader.getProperty("database-url");
    public static final String CREDENTIALS = PropertiesLoader.getProperty("credentials-file-path") ;

}
