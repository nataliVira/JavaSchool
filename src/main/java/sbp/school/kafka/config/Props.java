package sbp.school.kafka.config;

import java.io.FileInputStream;
import java.util.Properties;

public class Props {

    private Props() {
    }

    public static Properties getProperties() {
        String appProperties = Thread.currentThread().getContextClassLoader()
                .getResource("application.yaml").getPath();
        java.util.Properties appProps = new java.util.Properties();
        try {
            appProps.load(new FileInputStream(appProperties));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return appProps;
    }
}
