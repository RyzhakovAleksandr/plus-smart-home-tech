package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.TemperatureSensorEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.practicum.service.mapper.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorHandler extends BaseSensorHandler {
    public TemperatureSensorHandler(KafkaEventProducer kafkaProducer,
                                    KafkaConfig kafkaConfig,
                                    SensorEventAvroMapper avroMapper,
                                    SensorEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageSensorType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        TemperatureSensorAvro avro = avroMapper.mapTemperatureSensorToAvro((TemperatureSensorEvent) sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        TemperatureSensorEvent sensor = protoMapper.mapTemperatureSensorProtoToModel(sensorProto.getTemperatureSensor());
        return mapBaseSensorProtoFieldsToSensor(sensor, sensorProto);
    }
}
