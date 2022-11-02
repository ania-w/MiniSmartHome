package Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class PropertiesLoader {

    private static final Properties configuration=new Properties();

    private PropertiesLoader(){}

    static {
        try {
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
    }

    public static String getProperty(String key){
        return configuration.getProperty(key);
    }

}
