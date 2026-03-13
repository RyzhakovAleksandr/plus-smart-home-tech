package ru.yandex.practicum.service;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class SensorEventHandlerFactory {
    private final Map<String, SensorEventHandler> sensorHandlerMap;

    public SensorEventHandlerFactory(Set<SensorEventHandler> handlers) {
        this.sensorHandlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getSensorType,
                        Function.identity()
                ));
    }
}
