package sbp.kafka.consumer.confirm.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import sbp.kafka.consumer.confirm.constants.Constants;
import sbp.kafka.consumer.service.HandlerRecord;
import sbp.school.kafka.config.Props;
import sbp.school.kafka.service.KafkaProdeucerService;
import sbp.school.kafka.service.SendingService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HandleRecordService extends HandlerRecord {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(HandleRecordService.class);

    public HandleRecordService() {
        if (Constants.isStarted.compareAndSet(false, true)) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new SendConfirmdDtoScheduleService(new SendingService(new KafkaProdeucerService(Props.getProperties()) {
                        @Override
                        public void handleRecordMetadata(RecordMetadata recordMetadata, String key, String value, String kafkaProducerId) {

                        }
                    })),
                    0, 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void handleRecord(ConsumerRecord<String, String> record) {
        log.info("Recors topic = {}, offset = {}, partition = {} key = {} value = {}", record.topic(), record.offset(), record.partition());
        StorageRepository.addRecord(record);
    }

}
