package Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties prop = null;

    public static String getEnvData(String key){
        return prop.getProperty(key);
    }

    public void loadEnvProperties(String env){
        try {
            prop = new Properties();
            String filePath = System.getProperty("user.dir")+"\\src\\test\\resources\\"+env+"_config.properties";
            InputStream inputStream = new FileInputStream(new File(filePath));
            prop.load(inputStream);
        } catch(Exception ee){
                System.out.println("Unable to load environment properties");
        }
    }

}
