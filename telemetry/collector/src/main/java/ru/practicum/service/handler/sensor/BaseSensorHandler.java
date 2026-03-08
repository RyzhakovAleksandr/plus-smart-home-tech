package ru.practicum.service.handler.sensor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.config.KafkaConfig;
import ru.practicum.messages.Messages;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseSensorHandler implements SensorEventHandler {
    KafkaEventProducer producer;
    String topic;
    final SensorEventAvroMapper avroMapper;
    final SensorEventProtoMapper protoMapper;

    protected abstract SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent);
    protected abstract SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto);

    public BaseSensorHandler(KafkaEventProducer kafkaProducer,
                             KafkaConfig kafkaConfig,
                             SensorEventAvroMapper avroMapper,
                             SensorEventProtoMapper protoMapper) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("sensors-events");
        this.avroMapper = avroMapper;
        this.protoMapper = protoMapper;
        if (topic == null) {
            throw new IllegalArgumentException(Messages.ERROR_NOT_TOPIC_SENSOR);
        }
    }

    @Override
    public void handle(SensorEventProto sensorProto) {
        try {
            SensorEvent sensor = mapSensorProtoToModel(sensorProto);
            log.trace(Messages.SENSOR_MAP, sensor.getHubId());

            SensorEventAvro avro = mapSensorEventToAvro(sensor);
            log.trace(Messages.SENSOR_MAP_TO_AVRO, sensor.getHubId());

            sendToKafka(avro, sensor.getHubId(), sensor.getTimestamp());

        } catch (Exception e) {
            log.error(Messages.ERROR_EVENT_KAFKA, sensorProto, e);
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    protected void sendToKafka(SensorEventAvro avro, String sensorId, Instant timestamp) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                sensorId,
                avro
        );

        producer.sendRecord(record);
        log.info(Messages.SENSOR_EVENT_SENT, sensorId, topic);
    }

    protected SensorEventAvro buildSensorEventAvro(SensorEvent sensorEvent, SpecificRecordBase payloadAvro) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected SensorEvent mapBaseSensorProtoFieldsToSensor(SensorEvent sensor, SensorEventProto sensorProto) {
        sensor.setId(sensorProto.getId());
        sensor.setHubId(sensorProto.getHubId());

        long seconds = sensorProto.getTimestamp().getSeconds();
        int nanos = sensorProto.getTimestamp().getNanos();
        sensor.setTimestamp(Instant.ofEpochSecond(seconds, nanos));

        return sensor;
    }

}
