package sbp.kafka.consumer.confirm.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.confirm.dto.RecordDto;
import sbp.school.kafka.service.CheckSumService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Класс хрянящий сообщения отправленные в кафку и время отправки
 *
 * @version 1.0
 */
public class StorageRepository {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(StorageRepository.class);
    static Map<String, RecordDto> map = new ConcurrentHashMap<>();

    public static Set<Map.Entry<String, RecordDto>> getAll() {
        return map.entrySet();
    }

    public static final String PRODUCER_ID_KEY = "id";

    /**
     * Метод добавления в базу данных информации о сообщений, полученном из kafka
     *
     * @param record сообщения из брокера kafka
     */
    public static void addRecord(ConsumerRecord<String, String> record) {

        Headers headers = record.headers();
        Iterable<Header> idProducer = headers.headers(PRODUCER_ID_KEY);
        AtomicReference<String> kafkaProducerId = new AtomicReference<>();
        idProducer.forEach(h -> kafkaProducerId.set(new String(h.value())));
        logger.info("Headers = {} iterable {}", kafkaProducerId, Arrays.toString(headers.toArray()));
        RecordDto recordDto = new RecordDto(record.timestamp(), CheckSumService.getCRC32Checksum(record.value().getBytes()), kafkaProducerId.get());
        map.put(getKey(record.topic(), String.valueOf(record.partition()), String.valueOf(record.offset())), recordDto);
        logger.info("Added record: checksum {} value = {} timestamp {} producerId {}", recordDto.getCheckSum(), record.value(), recordDto.getTimestamp(), recordDto.getKafkaProducerId());
    }

    /**
     * Метод удаления сообщений из хранилища
     *
     * @param recordKeys - key(= topic + partition + offset) сообщения
     */
    public static void deleteRecords(List<String> recordKeys) {
        for (String recordKey : recordKeys) {
            map.remove(recordKey);
            logger.info("Removed record {}", recordKey);
        }
    }


    /**
     * Метод генерации ключа записи в хранилище
     *
     * @param topic     - топик сообщения
     * @param partition - партиция сообщения
     * @param offset    - смещение сообщения в партиции
     */
    private static String getKey(String topic, String partition, String offset) {
        return topic + partition + offset;
    }

}
