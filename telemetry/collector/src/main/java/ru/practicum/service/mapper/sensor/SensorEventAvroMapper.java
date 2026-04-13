package ru.practicum.service.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.sensor.ClimateSensorEvent;
import ru.practicum.model.sensor.LightSensorEvent;
import ru.practicum.model.sensor.MotionSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.SwitchSensorEvent;
import ru.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorEventAvroMapper {

    ClimateSensorAvro mapClimateSensorToAvro(ClimateSensorEvent climateSensorEvent);

    LightSensorAvro mapLightSensorToAvro(LightSensorEvent lightSensorEvent);

    MotionSensorAvro mapMotionSensorToAvro(MotionSensorEvent motionSensorEvent);

    SwitchSensorAvro mapSwitchSensorToAvro(SwitchSensorEvent switchSensorEvent);

    TemperatureSensorAvro mapTemperatureSensorToAvro(TemperatureSensorEvent temperatureSensorEvent);

    @Named("mapToAvroSafe")
    default SpecificRecordBase mapToAvroSafe(SensorEvent event) {
        if (event == null) return null;

        if (event instanceof ClimateSensorEvent) {
            return mapClimateSensorToAvro((ClimateSensorEvent) event);
        } else if (event instanceof LightSensorEvent) {
            return mapLightSensorToAvro((LightSensorEvent) event);
        } else if (event instanceof MotionSensorEvent) {
            return mapMotionSensorToAvro((MotionSensorEvent) event);
        } else if (event instanceof SwitchSensorEvent) {
            return mapSwitchSensorToAvro((SwitchSensorEvent) event);
        } else if (event instanceof TemperatureSensorEvent) {
            return mapTemperatureSensorToAvro((TemperatureSensorEvent) event);
        }

        throw new IllegalArgumentException("Unknown sensor type: " + event.getClass());
    }
}