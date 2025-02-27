package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.exception.BadParameterException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static sbp.school.kafka.enums.OperationTypeEnum.*;


class SendingServiceTest {

    @Test
    void sendToKafka() throws BadParameterException, IOException {
        KafkaProdeucerService kafkaProdeucerService = new KafkaProdeucerService(Props.getProperties()) {
            @Override
            public void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {

            }
        };

        SendingService sendingService = new SendingService(kafkaProdeucerService);
        Transaction transaction1 = new Transaction();
        transaction1.setOperationType(WITHDRAWAL);
        transaction1.setSum(new BigDecimal("0.1"));
        transaction1.setAccount("11111111111");
        transaction1.setDate(LocalDateTime.of(2024, 12, 13, 0, 0, 0));
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("====> " + mapper.writeValueAsString(transaction1));

        sendingService.sendToKafka(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setOperationType(ENROLLMENT);
        transaction2.setSum(new BigDecimal("5.0"));
        transaction2.setAccount("11111111111");
        transaction2.setDate(LocalDateTime.of(2024, 12, 13, 0, 0, 0));
        sendingService.sendToKafka(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setOperationType(TRANSFER);
        transaction3.setSum(new BigDecimal(3.5));
        transaction3.setAccount("11111111111");
        transaction3.setDate(LocalDateTime.of(2024, 12, 13, 0, 0, 0));
        sendingService.sendToKafka(transaction3);
        sendingService.close();
    }

}