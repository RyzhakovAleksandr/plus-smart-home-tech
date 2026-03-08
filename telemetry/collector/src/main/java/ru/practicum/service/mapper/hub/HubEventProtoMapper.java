package ru.practicum.service.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;
import ru.practicum.model.hub.DeviceAction;
import ru.practicum.model.hub.DeviceAddedEvent;
import ru.practicum.model.hub.DeviceRemovedEvent;
import ru.practicum.model.hub.ScenarioAddedEvent;
import ru.practicum.model.hub.ScenarioCondition;
import ru.practicum.model.hub.ScenarioRemovedEvent;
import ru.practicum.model.hub.enums.ActionType;
import ru.practicum.model.hub.enums.ConditionOperation;
import ru.practicum.model.hub.enums.ConditionType;
import ru.practicum.model.hub.enums.DeviceType;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HubEventProtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "deviceType", source = "type")
    @Mapping(target = "hubId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    DeviceAddedEvent mapDeviceAddedProtoToModel(DeviceAddedEventProto deviceAddedEventProto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    DeviceRemovedEvent mapDeviceRemovedProtoToModel(DeviceRemovedEventProto deviceRemovedEventProto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "conditions", source = "conditionList")
    @Mapping(target = "actions", source = "actionList")
    @Mapping(target = "hubId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    ScenarioAddedEvent mapScenarioAddedProtoToModel(ScenarioAddedEventProto scenarioAddedEventProto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "hubId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    ScenarioRemovedEvent mapScenarioRemovedProtoToModel(ScenarioRemovedEventProto scenarioRemovedEventProto);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", expression = "java(mapValue(scenarioConditionProto))")
    ScenarioCondition mapScenarioConditionProtoToModel(ScenarioConditionProto scenarioConditionProto);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    DeviceAction mapDeviceActionProtoToModel(DeviceActionProto deviceActionProto);

    // DeviceType маппер
    @ValueMapping(target = "MOTION_SENSOR", source = "DEVICE_TYPE_PROTO_MOTION_SENSOR")
    @ValueMapping(target = "TEMPERATURE_SENSOR", source = "DEVICE_TYPE_PROTO_TEMPERATURE_SENSOR")
    @ValueMapping(target = "LIGHT_SENSOR", source = "DEVICE_TYPE_PROTO_LIGHT_SENSOR")
    @ValueMapping(target = "CLIMATE_SENSOR", source = "DEVICE_TYPE_PROTO_CLIMATE_SENSOR")
    @ValueMapping(target = "SWITCH_SENSOR", source = "DEVICE_TYPE_PROTO_SWITCH_SENSOR")
    @ValueMapping(target = "MOTION_SENSOR", source = "UNRECOGNIZED")
    DeviceType mapDeviceTypeProtoToModel(DeviceTypeProto deviceTypeProto);

    @ValueMapping(target = "MOTION", source = "CONDITION_TYPE_PROTO_MOTION")
    @ValueMapping(target = "LUMINOSITY", source = "CONDITION_TYPE_PROTO_LUMINOSITY")
    @ValueMapping(target = "SWITCH", source = "CONDITION_TYPE_PROTO_SWITCH")
    @ValueMapping(target = "TEMPERATURE", source = "CONDITION_TYPE_PROTO_TEMPERATURE")
    @ValueMapping(target = "CO2LEVEL", source = "CONDITION_TYPE_PROTO_CO2LEVEL")
    @ValueMapping(target = "HUMIDITY", source = "CONDITION_TYPE_PROTO_HUMIDITY")
    @ValueMapping(target = "MOTION", source = "UNRECOGNIZED")
    ConditionType mapConditionTypeProtoToModel(ConditionTypeProto conditionTypeProto);

    @ValueMapping(target = "EQUALS", source = "CONDITION_OPERATION_PROTO_EQUALS")
    @ValueMapping(target = "GREATER_THAN", source = "CONDITION_OPERATION_PROTO_GREATER_THAN")
    @ValueMapping(target = "LOWER_THAN", source = "CONDITION_OPERATION_PROTO_LOWER_THAN")
    @ValueMapping(target = "EQUALS", source = "UNRECOGNIZED")
    ConditionOperation mapConditionOperationProtoToModel(ConditionOperationProto conditionOperationProto);

    @ValueMapping(target = "ACTIVATE", source = "ACTION_TYPE_PROTO_ACTIVATE")
    @ValueMapping(target = "DEACTIVATE", source = "ACTION_TYPE_PROTO_DEACTIVATE")
    @ValueMapping(target = "INVERSE", source = "ACTION_TYPE_PROTO_INVERSE")
    @ValueMapping(target = "SET_VALUE", source = "ACTION_TYPE_PROTO_SET_VALUE")
    @ValueMapping(target = "ACTIVATE", source = "UNRECOGNIZED")
    ActionType mapActionTypeProtoToModel(ActionTypeProto actionTypeProto);

    @Named("mapValue")
    default Integer mapValue(ScenarioConditionProto proto) {
        if (proto == null) {
            return null;
        }
        if (proto.hasBoolValue()) {
            return proto.getBoolValue() ? 1 : 0;
        } else if (proto.hasIntValue()) {
            return proto.getIntValue();
        }
        return null;
    }
}
