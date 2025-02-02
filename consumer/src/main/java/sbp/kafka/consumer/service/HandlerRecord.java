package sbp.kafka.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.dto.Transaction;
import sbp.kafka.consumer.exception.BadParameterException;

import java.io.IOException;

/**
 * Класс по обработке сообщений, полученных из kafka
 *
 * @version 1.0
 */
public abstract class HandlerRecord {

    private final JsonValidationService jsonValidationService = new JsonValidationService();

    Logger log = LoggerFactory.getLogger(HandlerRecord.class);

    protected HandlerRecord() {
    }

    protected void handle(ConsumerRecord<String, String> record) {
        try {
            handle(record);
        } catch (Exception e) {
            handleErrorRecors(record, e);
        }
    }

    /**
     * Метод обработки сообщения из kafka. Реализуется пользователем
     *
     * @param record пара ключ/значение полученные из kafka {@link ConsumerRecord}
     */
    public abstract void handleRecord(ConsumerRecord<String, String> record);



    private void handleErrorRecors(ConsumerRecord<String, String> record, Exception e) {
        log.info("Recors topic = {}, offset = {}, partition = {} handled with error {}", record.topic(), record.offset(), record.partition(), e.getMessage());
    }

}

