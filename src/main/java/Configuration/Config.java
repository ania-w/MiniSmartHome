package Configuration;


import java.util.Properties;

public class Config {

    private static final Properties properties = PropertiesLoader.loadProperties();
    public static final String SENSORS_COLLECTION_PATH = properties.getProperty("sensor-collection-name");
    public static final String DIMMERS_COLLECTION_PATH = properties.getProperty("dimmer-collection-name");
    public static final String PROJECT_ID=properties.getProperty("project-id");
    public static final String DATABASE_URL=properties.getProperty("database-url");
    public static final String CREDENTIALS = properties.getProperty("credentials-file-path") ;

}
