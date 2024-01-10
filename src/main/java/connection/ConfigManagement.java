package connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: 18600355@student.hcmus.edu.vn - Tran Phi Long
 */
public class ConfigManagement {

    private static final String FILE_CONFIG = "./config/configuration.properties";

    public static Properties getConfig() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = ConfigManagement.class.getClassLoader()
                    .getResourceAsStream(FILE_CONFIG);

            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
