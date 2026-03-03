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

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseSensorHandler implements SensorEventHandler {
    KafkaEventProducer producer;
    String topic;

    public BaseSensorHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("sensors-events");
    }

    @Override
    public void handle(SensorEvent sensorEvent) {
        try {
            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(
                            topic,
                            null,
                            System.currentTimeMillis(),
                            sensorEvent.getHubId(),
                            mapToAvro(sensorEvent)
                    );
            producer.sendRecord(record);
        } catch (Exception e) {
            log.error(Messages.ERROR_EVENT_KAFKA, sensorEvent, e);
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    abstract SpecificRecordBase mapToAvro(SensorEvent sensorEvent);
}
