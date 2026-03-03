package ru.practicum.service.handler.sensor;

import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.enums.SensorEventType;

public interface SensorEventHandler {
    void handle(SensorEvent event);

    SensorEventType getMessageType();
}
