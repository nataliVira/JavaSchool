package sbp.kafka.consumer.config;

import java.io.FileInputStream;
import java.util.Properties;

public class PropsConsumer {

    private PropsConsumer() {
    }

    public static Properties getProperties() {
        String appProperties = Thread.currentThread().getContextClassLoader()
                .getResource("application-consumer.yaml").getPath();
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appProperties));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return appProps;
    }
}
