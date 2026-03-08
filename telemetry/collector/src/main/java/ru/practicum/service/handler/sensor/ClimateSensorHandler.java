package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.ClimateSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class ClimateSensorHandler extends BaseSensorHandler {
    public ClimateSensorHandler(KafkaEventProducer kafkaProducer,
                                KafkaConfig kafkaConfig,
                                SensorEventAvroMapper avroMapper,
                                SensorEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageSensorType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        ClimateSensorAvro avro = avroMapper.mapClimateSensorToAvro((ClimateSensorEvent) sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        ClimateSensorEvent sensor = protoMapper.mapClimateSensorProtoToModel(sensorProto.getClimateSensor());
        return mapBaseSensorProtoFieldsToSensor(sensor, sensorProto);
    }
}
