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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro mapDeviceAddedToAvro(DeviceAddedEvent deviceAddedEvent);

    @Mapping(target = "id", source = "id")
    DeviceRemovedEventAvro mapDeviceRemoveToAvro(DeviceRemovedEvent deviceRemovedEvent);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "conditions", source = "conditions")
    @Mapping(target = "actions", source = "actions")
    ScenarioAddedEventAvro mapScenarioAddedToAvro(ScenarioAddedEvent scenarioAddedEvent);

    @Mapping(target = "name", source = "name")
    ScenarioRemovedEventAvro mapScenarioRemovedToAvro(ScenarioRemovedEvent scenarioRemovedEvent);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", source = "value")
    ScenarioConditionAvro mapConditionToAvro(ScenarioCondition condition);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    DeviceActionAvro mapActionToAvro(DeviceAction action);
}
