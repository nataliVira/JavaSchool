package sbp.school.kafka.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Header;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.dto.Transaction;
import sbp.school.kafka.exception.BadParameterException;

import java.io.IOException;
import java.util.List;

/**
 * Класс по отправке входящих транзакций
 */
public class SendingService {

    private final ObjectMapper mapper;
    private final JsonValidationService jsonValidationService;

    private final KafkaProdeucerService kafkaProdeucerService;


    /**
     * Конструктор - создание нового объекта
     */
    public SendingService(KafkaProdeucerService kafkaProdeucerService) {
        this.jsonValidationService = new JsonValidationService();
        this.mapper = new ObjectMapper();
        this.kafkaProdeucerService = kafkaProdeucerService;
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
        kafkaProdeucerService.send(topic, transaction.getOperationType().name(), jsonTransaction);
    }

    /**
     * Метод отправки json объекта
     *
     * @param JSON представление объекта класса {@link Transaction}
     * @throws BadParameterException
     */
    public void sendToKafka(String key, String json) throws BadParameterException {
        String topic = Props.getProperties().getProperty("topic");
        if (topic == null) {
            throw new BadParameterException("The topic do not specify in properties");
        }
        kafkaProdeucerService.send(topic, key, json);
    }

    public List<Header> getHeaders() {
        return kafkaProdeucerService.getHeaders();
    }

    public String getKafkaProducerId() {
        return kafkaProdeucerService.getKafkaProducerId();
    }

    /**
     * Метод остановки producer
     */
    public void close() {
        this.kafkaProdeucerService.close();
    }

    /**
     * Метод принудительной отправки сообщений в брокер
     */
    public void flush() {
        this.kafkaProdeucerService.flush();
    }

}