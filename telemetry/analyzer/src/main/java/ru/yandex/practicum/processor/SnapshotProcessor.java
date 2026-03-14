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
            log.info("Получен сигнал завершения, инициируем остановку...");
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(TOPICS);

            while (true) {
                log.debug("Ожидание новых сообщений...");
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.debug("Обработка снепшота: topic={}, partition={}, offset={}, hubId={}",
                            record.topic(), record.partition(), record.offset(), record.key());

                    handleSnapshot(record);
                    manageOffsets(record, count, consumer);

                    count++;
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Критическая ошибка", e);

        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Analyzer завершил работу");
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count,
                               Consumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void handleSnapshot(ConsumerRecord<String, SensorsSnapshotAvro> record) {
/*        List<Scenario> scenariosToExecute = analyzerService.analyze(record.value());

        if (!scenariosToExecute.isEmpty()) {
            log.info("Найдено {} сценариев для выполнения", scenariosToExecute.size());
            log.info(">>> Передаю сценарии в HubEventServiceImpl.actionExecute()");
            hubEventService.actionExecute(scenariosToExecute);
        } else {
            log.info("⏭️ Нет сценариев для выполнения");
        }*/
        System.out.println(">>> SnapshotProcessor.handleSnapshot START");
        System.out.println(">>> Snapshot key: " + record.key());
        System.out.println(">>> Snapshot value: " + record.value());

        List<Scenario> scenariosToExecute = analyzerService.analyze(record.value());

        System.out.println(">>> AnalyzerService.analyze вернул " + scenariosToExecute.size() + " сценариев");

        if (!scenariosToExecute.isEmpty()) {
            System.out.println(">>> Вызываю hubEventService.actionExecute() с " + scenariosToExecute.size() + " сценариями");
            hubEventService.actionExecute(scenariosToExecute);
        } else {
            System.out.println(">>> Нет сценариев для выполнения");
        }

        System.out.println(">>> SnapshotProcessor.handleSnapshot END");
    }
}
