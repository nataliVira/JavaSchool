package sbp.school.kafka.service;

public class KafkaProducerPartitionerService {
    private KafkaProducerPartitionerService() {
    }

    public static int partition(Object key) {
        if ("WITHDRAWAL".equals(key)) {
            return 0;
        }
        if ("ENROLLMENT".equals(key)) {
            return 1;
        }
        if ("TRANSFER".equals(key)) {
            return 2;
        }
        return 0;
    }

}
