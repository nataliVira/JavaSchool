package sbp;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import sbp.kafka.consumer.config.PropsConsumer;
import sbp.kafka.consumer.confirm.service.HandleRecordService;
import sbp.kafka.consumer.service.KafkaReaderService;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class ConsumerMain
{
    public static void main( String[] args )
    {

        Executor executor = Executors.newSingleThreadExecutor();
        KafkaReaderService kafkaReaderService = new KafkaReaderService(new KafkaConsumer(PropsConsumer.getProperties()), new HandleRecordService() {
        });
        executor.execute(kafkaReaderService);
    }
}
