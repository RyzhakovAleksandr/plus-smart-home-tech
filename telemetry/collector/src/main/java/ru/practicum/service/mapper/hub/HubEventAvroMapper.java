package ru.practicum.service.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.hub.DeviceAction;
import ru.practicum.model.hub.DeviceAddedEvent;
import ru.practicum.model.hub.DeviceRemovedEvent;
import ru.practicum.model.hub.ScenarioAddedEvent;
import ru.practicum.model.hub.ScenarioCondition;
import ru.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubEventAvroMapper {

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro mapDeviceAddedToAvro(DeviceAddedEvent deviceAddedEvent);

    DeviceRemovedEventAvro mapDeviceRemoveToAvro(DeviceRemovedEvent deviceRemovedEvent);

    ScenarioAddedEventAvro mapScenarioAddedToAvro(ScenarioAddedEvent scenarioAddedEvent);

    ScenarioRemovedEventAvro mapScenarioRemovedToAvro(ScenarioRemovedEvent scenarioRemovedEvent);

    ScenarioConditionAvro mapConditionToAvro(ScenarioCondition condition);

    DeviceActionAvro mapActionToAvro(DeviceAction action);
}
