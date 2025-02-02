package sbp.kafka.producer.confirm.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import sbp.kafka.producer.confirm.dto.MessageDto;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Класс хрянящий сообщения отправленные в кафку и время отправки
 *
 * @version 1.0
 */
public class StorageRepository {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(StorageRepository.class);
    private static Map<String, MessageDto> mapRecords = new ConcurrentHashMap<>();


    public static List<String> getRecordKeysBetweenTime(long startTimestamp, long endTimestamp, String kafkaProducerId) {
        return mapRecords.entrySet().stream()
                .filter(entry -> startTimestamp <= entry.getValue().getTimestamp()
                        && entry.getValue().getTimestamp() < endTimestamp && kafkaProducerId.equals(entry.getValue().getKafkaProducerId()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static List<MessageDto> getMessageByRecordIds(List<String> recordIds) {
        return mapRecords.entrySet().stream().filter(entry -> recordIds.contains(entry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * Метод возвращает  keys(= topic + partition + offset) сообщений,
     * у которых истек срок получения подтверждений от ConsumerKafka
     *
     * @param recordKey - key(= topic + partition + offset) сообщения
     * @return {@link MessageDto} хранящий ключ/значение отправленного в kafka сообщения
     */
    public static MessageDto getMessage(String recordKey) {
        return mapRecords.get(recordKey);
    }


    /**
     * Метод добавления информации о сообщений, отправленном в kafka
     *
     * @param recordMetadata метаданные сообщения
     * @param key            ключ отправленного сообщения
     * @param value          отправленное в kafka сообщение
     */
    public static void addRecord(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {
        String keyRecord = getKey(recordMetadata);
        mapRecords.put(keyRecord, new MessageDto(key, value, kafkaProducerId, recordMetadata.timestamp()));
        logger.info("Added records: {} {} ", key, recordMetadata.timestamp());

    }

    /**
     * Метод удаления сообщений из хранилища
     *
     * @param recordKeys - key(= topic + partition + offset) сообщения
     */
    public static void deleteRecords(List<String> recordKeys) {
        for (String recordKey : recordKeys) {
            mapRecords.remove(recordKey);
            logger.info("Removed record {}", recordKey);
        }
    }

    /**
     * Метод генерации ключа записи в хранилище
     *
     * @param recordMetadata - метаданные сообщения
     */
    private static String getKey(RecordMetadata recordMetadata) {
        return recordMetadata.topic() + recordMetadata.partition() + recordMetadata.offset();
    }

}
