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

    MotionSensorEvent mapMotionSensorProtoToModel(MotionSensorProto motionSensorProto);

    TemperatureSensorEvent mapTemperatureSensorProtoToModel(TemperatureSensorProto temperatureSensorProto);

    LightSensorEvent mapLightSensorProtoToModel(LightSensorProto lightSensorProto);

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
}