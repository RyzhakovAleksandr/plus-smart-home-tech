package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.service.AnalyzerService;
import ru.yandex.practicum.service.HubEventServiceImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.kafka.config.KafkaConfiguration.SNAPSHOTS_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final AnalyzerService analyzerService;
    private final HubEventServiceImpl hubEventService;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final List<String> TOPICS = List.of(SNAPSHOTS_TOPIC);

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info(Message.SHUTDOWN_SIGNAL_RECEIVED);
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(TOPICS);

            while (true) {
                log.debug(Message.WAITING_FOR_MESSAGES);
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.debug(Message.PROCESSING_SNAPSHOT,
                            record.topic(), record.partition(), record.offset(), record.key());

                    handleSnapshot(record);
                    manageOffsets(record, count, consumer);

                    count++;
                }
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error(Message.CRITICAL_ERROR, e);

        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info(Message.CONSUMER_CLOSED);
                consumer.close();
                log.info(Message.ANALYZER_CLOSED);
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> consumerRecord, int processedCount,
                               Consumer<String, SensorsSnapshotAvro> kafkaConsumer) {
        currentOffsets.put(
                new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                new OffsetAndMetadata(consumerRecord.offset() + 1)
        );

        if (processedCount % 10 == 0) {
            kafkaConsumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn(Message.OFFSET_COMMIT_ERROR, offsets, exception);
                }
            });
        }
    }

    private void handleSnapshot(ConsumerRecord<String, SensorsSnapshotAvro> consumerRecord) {
        List<Scenario> scenariosToExecute = analyzerService.analyze(consumerRecord.value());

        if (!scenariosToExecute.isEmpty()) {
            log.info(Message.SCENARIOS_FOUND, scenariosToExecute.size());
            hubEventService.actionExecute(scenariosToExecute);
        } else {
            log.info(Message.NO_SCENARIOS_FOUND);
        }
    }
}
