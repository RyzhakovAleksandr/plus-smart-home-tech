package ru.practicum.service.handler.hub;

import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.enums.HubEventType;

public interface HubEventHandler {
    void handle(HubEvent event);

    HubEventType getMessageType();
}
