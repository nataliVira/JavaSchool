package sbp.kafka.consumer.confirm.dto;

public class RecordDto {

    long timestamp;
    long checkSum;
    String kafkaProducerId;

    public RecordDto(long timestamp, long checkSum, String kafkaProducerId) {
        this.timestamp = timestamp;
        this.checkSum = checkSum;
        this.kafkaProducerId = kafkaProducerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public String getKafkaProducerId() {
        return kafkaProducerId;
    }

    @Override
    public String toString() {
        return "RecordDto{" +
                "timestamp=" + timestamp +
                ", checkSum=" + checkSum +
                ", kafkaProducerId='" + kafkaProducerId + '\'' +
                '}';
    }
}
