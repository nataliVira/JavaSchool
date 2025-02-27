package sbp.kafka.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, позволяющий фиксировать смещения сообщений в брокере перед перебалансировкой
 *
 * @version 1.0
 */
public class HandlerRebalance implements ConsumerRebalanceListener {
    private KafkaConsumer<String, String> consumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(HandlerRebalance.class);

    public HandlerRebalance(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }


    /**
     * Метод добавления смещений в коллекцию, хранящую смещения
     *
     * @param topic название топика, из которого считалось сообщение
     * @param partition партиция топика, из которой считалось сообщение
     * @param offset номер следующего сообщения в topic и partition, из которых считалось сообщение
     */
    public void addOffsetToTrack(String topic, int partition, long offset){
        currentOffsets.put(
                new TopicPartition(topic, partition),
                new OffsetAndMetadata(offset, null));
    }

    /**
     * Метод фиксации смещений. Вызывается до начала перебалансировки и после завершения получения сообщений потребителем.
     *
     * @param partitions коллекция, хранящая смещения
     */
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
       logger.info("Lost partitions in rebalance. Committing current offsets: {}", currentOffsets);
       try {
           consumer.commitSync(currentOffsets);
       } catch (Exception e) {
           logger.error(e.getMessage(), e);
       } finally {
           currentOffsets.clear();
       }

    }

    /**
     * Метод для назначения консьюмеру смещения в партиции. Используется при хранении смещений в БД.
     * При  хранении смещений в БД необходимо в методе {@link #onPartitionsRevoked(Collection<TopicPartition>)} сохранить смещения в БД.
     *
     * @param partitions коллекция, хранящая смещения
     */
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
    }

    /**
     * Метод получения коллекции, хранящей смещения
     */
    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffsets() {
        return currentOffsets;
    }

}
