package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import sbp.school.kafka.enums.OperationTypeEnum;

import java.util.Map;

import static sbp.school.kafka.enums.OperationTypeEnum.*;

public class KafkaProducerPartitionerService implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
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

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

}
