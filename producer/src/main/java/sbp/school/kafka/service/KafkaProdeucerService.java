package sbp.school.kafka.service;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс по отправке сообщений в kafka
 *
 * @version 1.0
 */
public abstract class KafkaProdeucerService {

    public static final String PRODUCER_ID_KEY = "id";
    private final KafkaProducer<String, String> producer;

    private final String kafkaProducerId;

    private final List<Header> headers;

    public KafkaProdeucerService(Properties properties) {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get("bootstrap.servers"));
        producerProperties.put(ProducerConfig.ACKS_CONFIG, properties.get("acks"));
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.get("key.serializer"));
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.get("value.serializer"));
        producer = new KafkaProducer<>(producerProperties);

        kafkaProducerId = (String) properties.get(PRODUCER_ID_KEY);

        headers = new ArrayList<>();
        headers.add(new RecordHeader(PRODUCER_ID_KEY, kafkaProducerId.getBytes()));
    }


    /**
     * Метод асинхронной отправки сообщения в kafka
     *
     * @param topic название топика, в который отправляются данные
     * @param key   ключ сообщения
     * @param value сообщение
     */
    public void send(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, KafkaProducerPartitionerService.partition(key),
                key, value, headers);

        producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                System.out.println("Error " + e.getMessage() + " offset " + recordMetadata.offset() +
                        " partition " + recordMetadata.partition());
            } else {
                handleRecordMetadata(recordMetadata, key, value, getKafkaProducerId());
                System.out.println("Successful " + "Topic " + recordMetadata.topic() + " Offset " + recordMetadata.offset() +
                        " Partition " + recordMetadata.partition() + " key " + key + " value " + value);
            }
        });
    }

    public abstract void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId);

    /**
     * Метод остановки producer
     */
    public void close() {
        producer.close();
    }

    /**
     * Метод принудительной отправки сообщений в брокер
     */
    public void flush() {
        producer.flush();
    }

    public List<Header> getHeaders() {
        return this.headers;
    }

    public String getKafkaProducerId() {
        return this.kafkaProducerId;
    }
}
