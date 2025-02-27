package sbp.kafka.consumer.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import sbp.kafka.consumer.config.PropsConsumer;

/**
 * Класс по получению экземпляра KafkaConsumer
 *
 * @version 1.0
 */
public class KafkaConsumerService {

    public static Consumer getKafkaConsumer() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(PropsConsumer.getProperties());
        return consumer;
    }

    public static void close(KafkaConsumer kafkaConsumer) {
        kafkaConsumer.close();
    }

}
