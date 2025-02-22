package sbp.kafka.producer.confirm.service;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;

import sbp.kafka.consumer.service.JsonValidationService;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.exception.BadParameterException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static sbp.school.kafka.enums.OperationTypeEnum.*;

class SendingWithConfirmTest {
    @Test
    void sendToKafka() throws BadParameterException, IOException {

        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("consumerId", "1".getBytes()));

        JsonValidationService jsonValidationService = new JsonValidationService();

        SendingWithConfirm sendingService = new SendingWithConfirm(Props.getProperties());
        Transaction transaction1 = new Transaction();
        transaction1.setOperationType(WITHDRAWAL);
        transaction1.setSum(new BigDecimal("0.1"));
        transaction1.setAccount("11111111111");
        transaction1.setDate(LocalDateTime.of(2024, 12, 13, 0, 0, 0));
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
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}