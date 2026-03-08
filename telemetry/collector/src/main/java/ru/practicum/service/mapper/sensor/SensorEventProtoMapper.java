package ru.practicum.service.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.sensor.ClimateSensorEvent;
import ru.practicum.model.sensor.LightSensorEvent;
import ru.practicum.model.sensor.MotionSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.SwitchSensorEvent;
import ru.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

import com.google.protobuf.Timestamp;
import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorEventProtoMapper {

    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "motion", source = "motion")
    @Mapping(target = "voltage", source = "voltage")
    MotionSensorEvent mapMotionSensorProtoToModel(MotionSensorProto motionSensorProto);

    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "temperatureF", source = "temperatureF")
    TemperatureSensorEvent mapTemperatureSensorProtoToModel(TemperatureSensorProto temperatureSensorProto);

    @Mapping(target = "linkQuality", source = "linkQuality")
    @Mapping(target = "luminosity", source = "luminosity")
    LightSensorEvent mapLightSensorProtoToModel(LightSensorProto lightSensorProto);

    @Mapping(target = "temperatureC", source = "temperatureC")
    @Mapping(target = "humidity", source = "humidity")
    @Mapping(target = "co2Level", source = "co2Level")
    ClimateSensorEvent mapClimateSensorProtoToModel(ClimateSensorProto climateSensorProto);

    SwitchSensorEvent mapSwitchSensorProtoToModel(SwitchSensorProto switchSensorProto);

    default SensorEvent mapSensorEventProtoToModel(SensorEventProto eventProto) {
        SensorEvent event = mapPayloadToModel(eventProto);

        if (event != null) {
            event.setId(eventProto.getId());
            event.setHubId(eventProto.getHubId());
            event.setTimestamp(mapTimestampToInstant(eventProto.getTimestamp()));
        }

        return event;
    }

    private SensorEvent mapPayloadToModel(SensorEventProto eventProto) {
        switch (eventProto.getPayloadCase()) {
            case MOTION_SENSOR:
                return mapMotionSensorProtoToModel(eventProto.getMotionSensor());
            case TEMPERATURE_SENSOR:
                return mapTemperatureSensorProtoToModel(eventProto.getTemperatureSensor());
            case LIGHT_SENSOR:
                return mapLightSensorProtoToModel(eventProto.getLightSensor());
            case CLIMATE_SENSOR:
                return mapClimateSensorProtoToModel(eventProto.getClimateSensor());
            case SWITCH_SENSOR:
                return mapSwitchSensorProtoToModel(eventProto.getSwitchSensor());
            default:
                return null;
        }
    }

    default Instant mapTimestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    default Timestamp mapInstantToTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}