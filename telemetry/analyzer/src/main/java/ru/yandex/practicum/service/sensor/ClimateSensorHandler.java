package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class ClimateSensorHandler implements SensorEventHandler {

    @Override
    public String getSensorType() {
        return ClimateSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType conditionType, SensorStateAvro state) {
        ClimateSensorAvro climate = (ClimateSensorAvro) state.getData();
        return switch (conditionType) {
            case TEMPERATURE -> climate.getTemperatureC();
            case HUMIDITY -> climate.getHumidity();
            case CO2LEVEL -> climate.getCo2Level();
            default -> null;
        };
    }
}
