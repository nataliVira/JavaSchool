package sbp.kafka.consumer.service;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaReaderServiceTest {

    private static final String TOPIC = "audit";
    private MockConsumer<String, String> mockConsumer;

    @BeforeEach
    void setUp_() {
        mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
    }


    @Test
    public void testTestRun() {
        List<ConsumerRecord<String, String>>  records = new ArrayList<>();

        mockConsumer.schedulePollTask(() -> {
            mockConsumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
            mockConsumer.addRecord(new ConsumerRecord<>(TOPIC, 0, 0, "key", "transaction"));
        });


        mockConsumer.schedulePollTask(() -> mockConsumer.setPollException(new CommitFailedException("test")));

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, 0);
        startOffsets.put(tp, 0L);
        mockConsumer.updateBeginningOffsets(startOffsets);

        HandlerRecord mockHandleRecord = mock(HandlerRecord.class);
        doAnswer(invocationOnMock ->
        {
            ConsumerRecord consumerRecord = invocationOnMock.getArgument(0);
            System.out.println("=====> invocation = " + consumerRecord);
            records.add(consumerRecord);
            return consumerRecord;
        }).when(mockHandleRecord).handleRecord(any());

        KafkaReaderService kafkaReaderService1 = new KafkaReaderService(mockConsumer, mockHandleRecord);
        kafkaReaderService1.run();

        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals("transaction", records.get(0).value());
        Assertions.assertTrue(mockConsumer.closed());
    }

}