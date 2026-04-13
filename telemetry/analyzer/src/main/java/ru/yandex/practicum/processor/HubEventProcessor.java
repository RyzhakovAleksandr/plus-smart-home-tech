package ru.yandex.practicum.processor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.List;

import static ru.yandex.practicum.kafka.config.KafkaConfiguration.HUBS_EVENTS_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventService hubEventService;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final List<String> TOPICS = List.of(HUBS_EVENTS_TOPIC);

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info(Message.SHUTDOWN_SIGNAL_RECEIVED);
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(TOPICS);

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.debug(Message.PROCESSING_HUB_EVENT,
                            record.topic(), record.partition(), record.offset(), record.key());

                    handleHubEvent(record);
                }
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error(Message.CRITICAL_ERROR, e);

        } finally {
            try {
                consumer.close();
                log.info(Message.CONSUMER_CLOSED);
            } catch (Exception e) {
                log.error(Message.CONSUMER_CLOSE_ERROR, e);
            }
        }
    }

    private void handleHubEvent(ConsumerRecord<String, HubEventAvro> consumerRecord) {
        try {
            HubEventAvro hubEvent = consumerRecord.value();
            hubEventService.processEvent(hubEvent);
        } catch (Exception e) {
            log.error(Message.MESSAGE_PROCESSING_ERROR, consumerRecord.offset(), e.getMessage(), e);
        }
    }

}
