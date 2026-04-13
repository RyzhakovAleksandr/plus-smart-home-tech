package ru.yandex.practicum.service.handel.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class MotionSensorHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return MotionSensorAvro.class.getName();
    }

    @Override
    public Integer getValue(ConditionType conditionType, SensorStateAvro sensorState) {
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) sensorState.getData();
        return switch (conditionType) {
            case MOTION -> motionSensorAvro.getMotion() ? 1 : 0;
            default -> null;
        };
    }
}