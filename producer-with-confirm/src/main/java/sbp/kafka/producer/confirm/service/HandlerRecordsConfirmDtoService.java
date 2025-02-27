package sbp.kafka.producer.confirm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.service.HandlerRecord;
import sbp.kafka.producer.confirm.dto.ConfirmDto;
import sbp.kafka.producer.confirm.dto.MessageDto;
import sbp.school.kafka.service.CheckSumService;

import sbp.school.kafka.service.SendingService;

import java.util.List;


/**
 * Класс обработки подтверждений, полученных от KafkaConsumer
 *
 * @version 1.0
 */
public class HandlerRecordsConfirmDtoService extends HandlerRecord {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(HandlerRecordsConfirmDtoService.class);

    private final static ObjectMapper mapper = new ObjectMapper();
    private final SendingService sendingService;

    public HandlerRecordsConfirmDtoService(SendingService sendingService) {
        super();
        this.sendingService = sendingService;
    }

    /**
     * Метод обработки подтверждений. Сравнивает контрольные суммы отправленных сообщений с контрольной суммой из подтверждения от KafkaConsumer,
     * при совпадении котрольных сумм удаляет сообщение из хранилища, при расхождении отправляет сообщение повторно. Контрольная сумма отправляется
     * для группы сообщений, попадающих в интервал времени, указанный в классе {@link ConfirmDto}
     *
     * @param record - сообщение от KafkaConsumer подтверждающее получение
     */
    @Override
    public void handleRecord(ConsumerRecord<String, String> record) {

        try {
            logger.info("Key = {} topic {} offet {} partition {}", record.key(), record.topic(), record.offset(), record.partition());

            if (!record.key().equals(sendingService.getKafkaProducerId())) {
                return;
            }
            ConfirmDto confirmDto = mapper.readValue(record.value(), ConfirmDto.class);

            List<String> recordKeys = StorageRepository.getRecordKeysBetweenTime(confirmDto.getStartTimestamp(),
                    confirmDto.getEndTimestamp(), sendingService.getKafkaProducerId());
            List<MessageDto> messages = StorageRepository.getMessageByRecordIds(recordKeys, sendingService.getKafkaProducerId());
            long checksumFromRepo = messages.stream().mapToLong(dto -> {
                long checksum = CheckSumService.getCRC32Checksum(dto.getValue().getBytes());
                System.out.println("Checksum = " + checksum + " value = " + dto.getValue());
                return checksum;
            }).sum();
            logger.info("Checksum {}  received checksum {}", checksumFromRepo, confirmDto.getCheckSum());

            if (checksumFromRepo != confirmDto.getCheckSum()) {
                for (MessageDto messageDto : messages) {
                    sendingService.sendToKafka(messageDto.getKeyMessage(), messageDto.getValue());
                }
            }
            StorageRepository.deleteRecords(recordKeys);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
