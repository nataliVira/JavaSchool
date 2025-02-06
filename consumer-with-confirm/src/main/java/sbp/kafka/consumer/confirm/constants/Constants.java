package sbp.kafka.consumer.confirm.constants;

import java.util.concurrent.atomic.AtomicBoolean;

public class Constants {

    private Constants() {
    }

    public static final AtomicBoolean isStarted = new AtomicBoolean(false);


    public static final String PRODUCER_ID_KEY = "id";
}
