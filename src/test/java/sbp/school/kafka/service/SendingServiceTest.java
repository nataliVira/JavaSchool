package sbp.school.kafka.service;

import org.junit.jupiter.api.Test;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.exception.BadParameterException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static sbp.school.kafka.enums.OperationTypeEnum.*;


class SendingServiceTest {

    @Test
    void sendToKafka() throws BadParameterException, IOException {
        JsonValidationService jsonValidationService = new JsonValidationService();

        SendingService sendingService = new SendingService(jsonValidationService);
        Transaction transaction1 = new Transaction();
        transaction1.setOperationType(WITHDRAWAL);
        transaction1.setSum(0.1);
        transaction1.setAccount("11111111111");
        transaction1.setDate("13.12.2024");
        sendingService.sendToKafka(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setOperationType(ENROLLMENT);
        transaction2.setSum(5.0);
        transaction2.setAccount("11111111111");
        transaction2.setDate("13.12.2024");
        sendingService.sendToKafka(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setOperationType(TRANSFER);
        transaction3.setSum(3.5);
        transaction3.setAccount("11111111111");
        transaction3.setDate("13.12.2024");
        sendingService.sendToKafka(transaction3);
        sendingService.close();
    }

}