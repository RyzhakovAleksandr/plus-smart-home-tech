package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.SwitchSensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorHandler extends BaseSensorHandler {
    public SwitchSensorHandler(KafkaEventProducer kafkaProducer,
                               KafkaConfig kafkaConfig,
                               SensorEventAvroMapper avroMapper,
                               SensorEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageSensorType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        SwitchSensorAvro avro = avroMapper.mapSwitchSensorToAvro((SwitchSensorEvent) sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        SensorEvent sensor = protoMapper.mapSwitchSensorProtoToModel(sensorProto.getSwitchSensor());
        return mapBaseSensorProtoFieldsToSensor(sensor, sensorProto);
    }
}
