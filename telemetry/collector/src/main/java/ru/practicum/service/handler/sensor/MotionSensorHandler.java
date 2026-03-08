package ru.practicum.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.MotionSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
public class MotionSensorHandler extends BaseSensorHandler {
    public MotionSensorHandler(KafkaEventProducer kafkaProducer,
                               KafkaConfig kafkaConfig,
                               SensorEventAvroMapper avroMapper,
                               SensorEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageSensorType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        MotionSensorAvro avro = avroMapper.mapMotionSensorToAvro((MotionSensorEvent) sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        SensorEvent sensor = protoMapper.mapMotionSensorProtoToModel(sensorProto.getMotionSensor());
        return mapBaseSensorProtoFieldsToSensor(sensor, sensorProto);
    }
}
