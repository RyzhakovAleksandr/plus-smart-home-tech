package ru.yandex.practicum.service.handel.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class SwitchSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return SwitchSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType conditionType, SensorStateAvro sensorState) {
        SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) sensorState.getData();
        return switch (conditionType) {
            case SWITCH -> switchSensorAvro.getState() ? 1 : 0;
            default -> 0;
        };
    }
}
