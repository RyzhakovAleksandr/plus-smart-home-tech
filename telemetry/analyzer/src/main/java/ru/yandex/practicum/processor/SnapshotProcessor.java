package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import ru.yandex.practicum.service.snapshot.SnapshotHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final AnalyzerService analyzerService;
    private final SnapshotHandler snapshotHandler;
    private volatile boolean isRunning = true;

    @Value("${kafka.snapshot-config.topic}")
    private String topic;

    public void start() {
        consumer.subscribe(List.of(topic));
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            while (isRunning) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
            log.info(Message.INFO_CONSUMER_STOPPED);
        } catch (WakeupException ignored) {
            log.warn(Message.WARN_CONSUMER_WOKEN);
        } catch (Exception exp) {
            log.error(Message.ERROR_KAFKA_CONSUME, topic, exp);
        } finally {
            try {
                log.info(Message.INFO_CONSUMER_STOPPING);
                consumer.close();
            } catch (Exception exp) {
                log.warn(Message.WARN_CONSUMER_CLOSE_ERROR, exp);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        isRunning = false;
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro snapshot = record.value();
        log.info(Message.INFO_SNAPSHOT_RECEIVED, snapshot);
        List<Scenario> scenariosToExecute = analyzerService.analyze(snapshot);
        if (!scenariosToExecute.isEmpty()) {
            log.info(Message.INFO_SCENARIO_FOUND, scenariosToExecute.size());
            snapshotHandler.sendActions(scenariosToExecute);
        }
    }
}
