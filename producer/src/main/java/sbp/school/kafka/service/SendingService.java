package sbp.school.kafka.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.enums.OperationTypeEnum;
import sbp.school.kafka.exception.BadParameterException;

import java.io.IOException;

import static sbp.school.kafka.enums.OperationTypeEnum.*;

/**
 * Класс по отправке входящих транзакций
 */
public class SendingService {

    private static final String OPERATION_TYPE = "operationType";
    private final ObjectMapper mapper;
    private final JsonValidationService jsonValidationService;

    /**
     * Конструктор - создание нового объекта
     */
    public SendingService(JsonValidationService jsonValidationService) {
        this.jsonValidationService = jsonValidationService;
        this.mapper = new ObjectMapper();
//        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Метод отправки объекта
     *
     * @param transaction JSON представление объекта класса {@link Transaction}
     * @throws JsonProcessingException
     * @throws BadParameterException
     */
    public void sendToKafka(Transaction transaction) throws IOException, BadParameterException {
        String jsonTransaction = mapper.writeValueAsString(transaction);
        boolean isValidate = jsonValidationService.validate(jsonTransaction);
        if (!isValidate) {
            throw new BadParameterException("Invalid object passed: " + jsonTransaction);
        }
        String topic = Props.getProperties().getProperty("topic");
        if (topic == null) {
            throw new BadParameterException("The topic do not specify in properties");
        }
        KafkaProdeucerService.send(topic, transaction.getOperationType(), jsonTransaction);
    }

//    private static int getPartition(OperationTypeEnum key) throws BadParameterException {
//        if (WITHDRAWAL == key) {
//            return 0;
//        }
//        if (ENROLLMENT == key) {
//            return 1;
//        }
//        if (TRANSFER == key) {
//            return 2;
//        }
//        throw new BadParameterException("Bad value of operation type");
//    }


    /**
     * Метод остановки producer
     */
    public void close() {
        KafkaProdeucerService.close();
    }

    /**
     * Метод принудительной отправки сообщений в брокер
     */
    public static void flush() {
        KafkaProdeucerService.flush();
    }

}