package sbp.kafka.consumer.confirm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.confirm.dto.ConfirmDto;
import sbp.kafka.consumer.confirm.dto.RecordDto;
import sbp.school.kafka.service.SendingService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * Класс отправки в кафку сообщений, для которых превышен интервал ожидания получения подтверждений от KafkaConsumer
 *
 * @version 1.0
 */
public class SendConfirmdDtoScheduleService implements Runnable {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(SendConfirmdDtoScheduleService.class);


    private final SendingService sendingService;

    private final ObjectMapper objectMapper;

    AtomicLong endTimeLast = new AtomicLong(0);


    public SendConfirmdDtoScheduleService(SendingService sendingService) {
        this.sendingService = sendingService;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public void run() {
        log.info("CheckOverScheduleService ======> started");
        try {
            List<String> kafkaProducerIds = StorageRepository.getAll().stream().map(entry -> entry.getValue().getKafkaProducerId())
                    .distinct().collect(Collectors.toList());
            for (String kafkaProducerId : kafkaProducerIds) {
                sendConfirmDto(kafkaProducerId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("CheckOverScheduleService ======> finished");
    }


    private void sendConfirmDto(String kafkaProducerId) {
        AtomicLong startTime = new AtomicLong(0);
        long endTime = System.currentTimeMillis() - 5 * 1000;//5 * 60 * 1000;

        if (endTimeLast.get() != 0) {
            startTime.set(endTimeLast.get());
        }

        endTimeLast.set(endTime);

        AtomicReference<List<String>> listRecordIds = new AtomicReference<>(new ArrayList<>());

        log.info("CheckOverScheduleService ======> started");
        try {
            Set<Map.Entry<String, RecordDto>> setRecords =  StorageRepository.getAll().stream()
                    .filter(entry -> isValid(entry, kafkaProducerId, endTime, startTime.get())).collect(Collectors.toSet());
            log.info("Valid records {}", Arrays.asList(setRecords.toArray()));
            if  (setRecords.size() > 0) {
                Long checkSum = setRecords.stream()
                        .mapToLong(entry -> {
                            if (startTime.get() == 0 || startTime.get() > entry.getValue().getTimestamp()) {
                                startTime.set(entry.getValue().getTimestamp());
                            }
                            listRecordIds.get().add(entry.getKey());
                            return entry.getValue().getCheckSum();
                        }).sum();
                ConfirmDto confirmDto = new ConfirmDto(checkSum, startTime.get(), endTime);
                String json = objectMapper.writeValueAsString(confirmDto);
                sendingService.sendToKafka(kafkaProducerId, json);
                StorageRepository.deleteRecords(listRecordIds.get());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isValid(Map.Entry<String, RecordDto> entry, String kafkaProducerId, long endTime, long startTime) {
        return entry.getValue().getKafkaProducerId().equals(kafkaProducerId)
                && startTime <= entry.getValue().getTimestamp() && entry.getValue().getTimestamp() < endTime;
    }
}
