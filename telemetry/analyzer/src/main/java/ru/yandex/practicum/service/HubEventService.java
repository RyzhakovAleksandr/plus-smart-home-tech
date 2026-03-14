package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

public interface HubEventService {
    void processEvent(HubEventAvro event);

    void actionExecute(List<Scenario> scenarios);
}
