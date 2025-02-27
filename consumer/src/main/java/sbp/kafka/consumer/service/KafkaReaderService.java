package sbp.kafka.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.config.PropsConsumer;
import sbp.kafka.consumer.dto.Transaction;
import sbp.kafka.consumer.exception.BadParameterException;


import java.time.Duration;
import java.util.Collections;

/**
 * Класс по чтению сообщений из kafka
 *
 * @version 1.0
 */
public class KafkaReaderService implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(KafkaReaderService.class);
    private final Consumer consumer;
    private final HandlerRecord handlerRecord;

    public KafkaReaderService(Consumer consumer, HandlerRecord handlerRecord) {
        this.consumer =consumer;
        this.handlerRecord = handlerRecord;
    }

    @Override
    public void run() {
        logger.info("KafkaReaderService started");
        HandlerRebalance handlerRebalance = new HandlerRebalance(consumer);
        consumer.subscribe(Collections.singletonList(PropsConsumer.getProperties().getProperty("topic")), handlerRebalance);
        int count = 0;
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    ++count;
                    handlerRebalance.addOffsetToTrack(record.topic(), record.partition(), record.offset() + 1);
                    logger.info("topic = {}, partition = {}, offset = {}, operationTypeEnum = {}, transaction = {}\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    handlerRecord.handleRecord(record);
                    if (count > 0 && count % 2 == 0) {
                        count = 0;
                        consumer.commitAsync(handlerRebalance.getCurrentOffsets(), (offsets, exception) -> {
                                    if (exception != null) {
                                        logger.error("commit failed for offsets {}", offsets, exception);
                                    } else {
                                        logger.info("commit async success for offsets {}", offsets);
                                    }
                                }
                        );
                    }
                }
            }
        } catch (WakeupException ex) {
            logger.info("Received shutdown signal");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                consumer.commitSync(handlerRebalance.getCurrentOffsets());
                logger.info("commit sync success for offsets {}", handlerRebalance.getCurrentOffsets());
            } catch (Exception e) {
                logger.error("commit sync failed for offsets: {}", e.getMessage());
            }
            consumer.close();
        }
    }

}
