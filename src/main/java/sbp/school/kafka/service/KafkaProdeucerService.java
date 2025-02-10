package sbp.school.kafka.service;


import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.LeaderNotAvailableException;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.enums.OperationTypeEnum;
import sbp.school.kafka.exception.BadParameterException;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Класс по отправке сообщений в kafka
 *
 * @version 1.0
 */
public class KafkaProdeucerService {

    static KafkaProducer<String, String> producer;

    static {
        Properties properties = Props.getProperties();
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get("bootstrap.servers"));
        producerProperties.put(ProducerConfig.ACKS_CONFIG, properties.get("acks"));
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.get("key.serializer"));
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.get("value.serializer"));
        producer = new KafkaProducer<>(producerProperties);
    }

    /**
     * Метод асинхронной отправки сообщения в kafka
     *
     * @param topic название топика, в который отправляются данные
     * @param key   ключ сообщения
     * @param value сообщение
     */
    public static void send(String topic, int partition, OperationTypeEnum key, String value) throws BadParameterException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, key.name(), value);
        producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                System.out.println("Error " + e.getMessage() + " offset " + recordMetadata.offset() +
                        " partition " + recordMetadata.partition());
            } else {
                System.out.println("Successful " + "Topic " + recordMetadata.topic() + " Offset " + recordMetadata.offset() +
                        " Partition " + recordMetadata.partition());
            }
        });
    }

    /**
     * Метод остановки producer
     */
    public static void close() {
        producer.close();
    }

    /**
     * Метод принудительной отправки сообщений в брокер
     */
    public static void flush() {
        producer.flush();
    }
}
