package ru.practicum.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.messages.Messages;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseHubHandler implements HubEventHandler {

    KafkaEventProducer producer;
    String topic;

    public BaseHubHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("hubs-events");
        if (topic == null) {
            throw new IllegalArgumentException(Messages.ERROR_NOT_TOPIC_HUB);
        }
    }

    @Override
    public void handle(HubEvent hubEvent) {
        try {
            SpecificRecordBase payload = mapToAvro(hubEvent);

            HubEventAvro avroEvent = HubEventAvro.newBuilder()
                    .setHubId(hubEvent.getHubId())
                    .setTimestamp(hubEvent.getTimestamp())
                    .setPayload(payload)
                    .build();

            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(
                            topic,
                            null,
                            hubEvent.getTimestamp().toEpochMilli(),
                            hubEvent.getHubId(),
                            avroEvent
                    );
            producer.sendRecord(record);
            log.debug(Messages.MESSAGE_SEND, hubEvent.getHubId());

        } catch (Exception e) {
            log.error(Messages.ERROR_EVENT_KAFKA, hubEvent, e);
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    abstract SpecificRecordBase mapToAvro(HubEvent hubEvent);
}
