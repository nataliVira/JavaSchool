package sbp.kafka.producer.confirm.dto;

public class MessageDto {

    String keyMessage;
    String value;

    String kafkaProducerId;

    long timestamp;

    public MessageDto(String keyMessage, String value, String kafkaProducerId, long timestamp) {
        this.keyMessage = keyMessage;
        this.value = value;
        this.kafkaProducerId = kafkaProducerId;
        this.timestamp = timestamp;
    }

    public String getKeyMessage() {
        return keyMessage;
    }

    public String getValue() {
        return value;
    }

    public String getKafkaProducerId() {
        return kafkaProducerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
