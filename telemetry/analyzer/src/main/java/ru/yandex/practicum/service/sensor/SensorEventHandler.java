package ru.yandex.practicum.service.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

public interface SensorEventHandler {
    String getSensorType();

    Integer getValue(ConditionType conditionType, SensorStateAvro state);
}
