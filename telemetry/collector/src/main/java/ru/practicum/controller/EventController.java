package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.Messages;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.enums.HubEventType;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.enums.SensorEventType;
import ru.practicum.service.handler.hub.HubEventHandler;
import ru.practicum.service.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final Map<HubEventType, HubEventHandler> hubEventHandlersMap;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlersMap;

    public EventController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlersMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlersMap = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hub) {
        log.info(Messages.HUB_EVENT, hub.getType());
        if (hubEventHandlersMap.containsKey(hub.getType())) {
            hubEventHandlersMap.get(hub.getType()).handle(hub);
            log.info(Messages.HUB_EVENT_OK);
        } else {
            log.error(Messages.HUB_EVENT_NOT_FOUND, hub.getType());
            throw new IllegalArgumentException(Messages.EXCEPTION_HUB_NOT_FOUND);
        }
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensor) {
        log.info(Messages.SENSOR_EVENT, sensor.getType());
        if (sensorEventHandlersMap.containsKey(sensor.getType())) {
            sensorEventHandlersMap.get(sensor.getType()).handle(sensor);
            log.info(Messages.SENSOR_EVENT_OK);
        } else {
            log.error(Messages.SENSOR_EVENT_NOT_FOUND, sensor.getType());
            throw new IllegalArgumentException(Messages.EXCEPTION_SENSOR_NOT_FOUND);
        }
    }
}
