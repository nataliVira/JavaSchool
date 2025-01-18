package sbp.kafka.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.dto.Transaction;
import sbp.kafka.consumer.exception.BadParameterException;

import java.io.IOException;

public class HandlerRecord {

    Logger log = LoggerFactory.getLogger(HandlerRecord.class);

    private final JsonValidationService jsonValidationService = new JsonValidationService();
    private final ObjectMapper mapper = new ObjectMapper();

    public void handleRecord(ConsumerRecord<String, String> record)  {
        try {
            boolean isValid = jsonValidationService.validate(record.value());
            if (isValid) {
                Transaction transaction = mapper.readValue(record.value(), Transaction.class);
                System.out.println("Transaction:" + transaction.toString());
            } else {
                throw new BadParameterException("Not valid JSON " + record.value());
            }
        } catch (Exception e) {
            handleErrorRecors(record, e);
        }
    }

    private void handleErrorRecors(ConsumerRecord<String, String> record, Exception e) {
        log.info("Recors topic = {}, offset = {}, partition = {} handled with error {}", record.topic(), record.offset(), record.partition(), e.getMessage());
    }

}
