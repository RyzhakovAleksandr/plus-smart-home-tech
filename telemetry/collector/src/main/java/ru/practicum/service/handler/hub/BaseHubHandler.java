package ru.practicum.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.Messages;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.service.handler.KafkaEventProducer;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseHubHandler implements HubEventHandler {

    KafkaEventProducer producer;
    String topic;

    public BaseHubHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("hubs-events");
    }

    @Override
    public void handle(HubEvent hubEvent) {
        try {
            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(
                            topic,
                            null,
                            System.currentTimeMillis(),
                            hubEvent.getHubId(),
                            mapToAvro(hubEvent)
                    );
            producer.sendRecord(record);
        } catch (Exception e) {
            log.error(Messages.ERROR_EVENT_KAFKA, hubEvent, e);
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    abstract SpecificRecordBase mapToAvro(HubEvent hubEvent);
}
