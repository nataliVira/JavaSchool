package sbp.kafka.producer.confirm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.RecordMetadata;
import sbp.kafka.consumer.service.KafkaConsumerService;
import sbp.kafka.consumer.service.KafkaReaderService;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.exception.BadParameterException;
import sbp.school.kafka.service.KafkaProdeucerService;
import sbp.school.kafka.service.SendingService;


import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SendingWithConfirm {

    private final SendingService sendingService;
    public SendingWithConfirm(Properties properties) {
        sendingService = new SendingService(new KafkaProdeucerService(properties) {
            @Override
            public void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {
                StorageRepository.addRecord(recordMetadata, key, value, kafkaProducerId);
            }
        });

        Executor executor = Executors.newSingleThreadExecutor();
        KafkaReaderService kafkaReaderService = new KafkaReaderService(KafkaConsumerService.getKafkaConsumer(),
                new HandlerRecordsConfirmDtoService(sendingService));
        executor.execute(kafkaReaderService);
    }

    /**
     * Метод отправки объекта
     *
     * @param transaction JSON представление объекта класса {@link Transaction}
     * @throws JsonProcessingException
     * @throws BadParameterException
     */
    public void sendToKafka(Transaction transaction) throws BadParameterException, IOException {
        sendingService.sendToKafka(transaction);
    }

    public void sendToKafka(String key, String json) throws Exception {
        sendingService.sendToKafka(key, json);
    }

    public String getKafkaProducerId() {
        return sendingService.getKafkaProducerId();
    }


    public void close() {
        sendingService.close();
    }

}
