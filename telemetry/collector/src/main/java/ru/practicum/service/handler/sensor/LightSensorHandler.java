package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.LightSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class LightSensorHandler extends BaseSensorHandler {
    public LightSensorHandler(KafkaEventProducer kafkaProducer,
                              KafkaConfig kafkaConfig,
                              SensorEventAvroMapper avroMapper,
                              SensorEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageSensorType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        LightSensorAvro avro = avroMapper.mapLightSensorToAvro((LightSensorEvent) sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        SensorEvent sensor = protoMapper.mapLightSensorProtoToModel(sensorProto.getLightSensor());
        return mapBaseSensorProtoFieldsToSensor(sensor, sensorProto);
    }
}
