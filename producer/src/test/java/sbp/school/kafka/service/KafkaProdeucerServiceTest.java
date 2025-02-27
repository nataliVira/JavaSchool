package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static sbp.school.kafka.service.KafkaProdeucerService.PRODUCER_ID_KEY;

class KafkaProdeucerServiceTest {

    private final String TOPIC = "audit";
    private final String KEY = "key";
    private final String VALUE = "value";
    MockProducer mockProducer;
    Properties producerProperties;

    @BeforeEach
    void setup() {
        this.mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        this.producerProperties = new Properties();
        this.producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,127.0.0.1:29092");
        this.producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        this.producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.producerProperties.put(PRODUCER_ID_KEY, "id");
    }


    @Test
    void whenConstructorInvokedWithoutInitializer_ThenMockObjectShouldBeCreatedWithNullFields() {
        try (MockedConstruction<KafkaProducer> mockKafkaProdeucerService = Mockito.mockConstruction(KafkaProducer.class)) {
            KafkaProdeucerService kafkaKafkaProdeucerService = new KafkaProdeucerService(producerProperties) {
                @Override
                public void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {

                }
            };
            Assertions.assertEquals(1, mockKafkaProdeucerService.constructed().size());
        }
    }

    @Test
    void send() {
        final ProducerRecord<String, String>[] record = new ProducerRecord[1];
        try (MockedConstruction<KafkaProducer> mockKafkaProducer = Mockito.mockConstruction(KafkaProducer.class, (mock, context) -> {
            doAnswer(invocation -> {
                ProducerRecord<String, String> result = invocation.getArgument(0);
                record[0] = result;
                return mockProducer.send(result);
            }).when(mock).send(any(), any());
        })) {
            KafkaProdeucerService kafkaProdeucerService = new KafkaProdeucerService(producerProperties) {
                @Override
                public void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {
                    System.out.println("====> key " + key + " value " + value + " kafkaProducerId " + kafkaProducerId);
                }
            };
            kafkaProdeucerService.send(TOPIC, KEY, VALUE);
            System.out.println("=====> " + record[0].toString());
            assertEquals(VALUE, record[0].value());
            assertTrue(0 == record[0].partition());
            assertEquals(1, mockProducer.history().size());
        }
    }

}