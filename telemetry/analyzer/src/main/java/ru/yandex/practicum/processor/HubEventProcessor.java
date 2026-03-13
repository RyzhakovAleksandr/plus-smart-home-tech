package ru.yandex.practicum.processor;


import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.service.HubEventService;
import ru.yandex.practicum.service.hub.HubEventHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventService hubEventService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private volatile boolean isRunning = true;

    @Value("${kafka.hub-event-config.topic}")
    private String topic;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (isRunning) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));
                int count = 0;

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    hubEventService.processEvent(record.value());
                    manageOffsets(record, count);
                    count++;
                }
                consumer.commitAsync();
            }
            log.info(Message.INFO_CONSUMER_STOPPED);
        } catch (WakeupException ignored) {
            log.warn(Message.WARN_CONSUMER_WOKEN);
        } catch (Exception exp) {
            log.error(Message.ERROR_KAFKA_CONSUME, topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info(Message.INFO_CONSUMER_STOPPING);
                consumer.close();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        isRunning = false;
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record, Map<String, HubEventHandler> handlerMap) {
        HubEventAvro event = record.value();
        String payloadName = event.getPayload().getClass().getSimpleName();
        log.info(Message.INFO_HUB_MESSAGE, payloadName);

        if (handlerMap.containsKey(payloadName)) {
            handlerMap.get(payloadName).handle(event);
        } else {
            throw new IllegalArgumentException(String.format(Message.ERROR_NO_HANDLER, event));
        }
    }

    private void manageOffsets(ConsumerRecord<String, HubEventAvro> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (count % 10 == 0) {
            log.debug("count={}", count);
            OptionalLong maxOptional = currentOffsets.values().stream()
                    .mapToLong(OffsetAndMetadata::offset)
                    .max();
            maxOptional.ifPresent(max -> log.debug(Message.DEBUG_OFFSET_COMMIT, max));

            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception == null) {
                    log.debug(Message.DEBUG_OFFSET_COMMIT_SUCCESS, offsets);
                } else {
                    log.error(Message.ERROR_OFFSET_COMMIT, offsets, exception);
                }
            });
        }
    }
}
