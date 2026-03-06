package ru.practicum.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.config.KafkaConfig;
import ru.practicum.messages.Messages;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseSensorHandler implements SensorEventHandler {
    KafkaEventProducer producer;
    String topic;

    public BaseSensorHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("sensors-events");
        if (topic == null) {
            throw new IllegalArgumentException(Messages.ERROR_NOT_TOPIC_SENSOR);
        }
    }

    @Override
    public void handle(SensorEvent sensorEvent) {
        try {
            SpecificRecordBase payload = mapToAvro(sensorEvent);

            SensorEventAvro avroEvent = SensorEventAvro.newBuilder()
                    .setId(sensorEvent.getId())
                    .setHubId(sensorEvent.getHubId())
                    .setTimestamp(sensorEvent.getTimestamp())
                    .setPayload(payload)
                    .build();

            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(
                            topic,
                            null,
                            sensorEvent.getTimestamp().toEpochMilli(),
                            sensorEvent.getHubId(),
                            avroEvent
                    );
            producer.sendRecord(record);
            log.debug(Messages.MESSAGE_SEND, sensorEvent.getId());

        } catch (Exception e) {
            log.error(Messages.ERROR_EVENT_KAFKA, sensorEvent, e);
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    abstract SpecificRecordBase mapToAvro(SensorEvent sensorEvent);
}
