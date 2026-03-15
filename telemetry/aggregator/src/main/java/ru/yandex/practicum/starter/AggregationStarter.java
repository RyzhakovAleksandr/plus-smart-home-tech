package ru.yandex.practicum.starter;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregation.AggregationEventSnapshot;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.messages.Message;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final AggregationEventSnapshot aggregationSnapshot;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Value("${topic.telemetry-sensors}")
    private String sensorsTopic;

    @Value("${aggregator.topic.telemetry-snapshots}")
    private String snapshotsTopic;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(sensorsTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                int processedCount = 0;
                for (ConsumerRecord<String, SpecificRecordBase> currentRecord : records) {
                    log.info(Message.LOG_PROCESS_NEXT, currentRecord.value());
                    handleRecord(currentRecord);
                    manageOffsets(currentRecord, processedCount, consumer);
                    processedCount++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignoredException) {
        } catch (Exception processingException) {
            log.error(Message.LOG_ERROR_EVENT, processingException);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info(Message.LOG_CLOSE_CONSUMER);
                consumer.close();
                log.info(Message.LOG_CLOSE_PRODUCER);
                producer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info(Message.LOG_RECORD_DETAILS,
                record.topic(), record.partition(), record.offset(), record.value());

        SensorEventAvro event = (SensorEventAvro) record.value();
        Optional<SensorsSnapshotAvro> snapshot = aggregationSnapshot.updateState(event);

        log.info(Message.LOG_SNAPSHOT_RECEIVED, snapshot);

        if (snapshot.isPresent()) {
            log.info(Message.LOG_WRITE_TO_TOPIC);
            ProducerRecord<String, SpecificRecordBase> producerRecord =
                    new ProducerRecord<>(snapshotsTopic,
                            null, event.getTimestamp().toEpochMilli(), event.getHubId(), snapshot.get());

            producer.send(producerRecord);
            log.info(Message.LOG_SNAPSHOT_UPDATED_SENT, snapshot);
        } else {
            log.info(Message.LOG_SNAPSHOT_NOT_UPDATED);
        }
    }

    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> consumerRecord, int processedCount,
                               Consumer<String, SpecificRecordBase> kafkaConsumer) {
        currentOffsets.put(
                new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                new OffsetAndMetadata(consumerRecord.offset() + 1)
        );

        if (processedCount % 10 == 0) {
            kafkaConsumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn(Message.LOG_OFFSET_COMMIT_ERROR, offsets, exception);
                }
            });
        }
    }
}
