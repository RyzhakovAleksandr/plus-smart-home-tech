package ru.yandex.practicum.service.handel.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

public interface SensorEventHandler {
    String getType();

    Integer getValue(ConditionType condition, SensorStateAvro snapshot);
}