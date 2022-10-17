package Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesLoader {

    public static Properties loadProperties() {

        Properties configuration=null;

        try {
            configuration = new Properties();
            InputStream inputStream = PropertiesLoader.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties");
            configuration.load(inputStream);
            inputStream.close();
        } catch (IOException e){
                System.err.println("Failed to load properties.");
                e.printStackTrace();
                System.exit(1);
            }

        return configuration;
    }
}
