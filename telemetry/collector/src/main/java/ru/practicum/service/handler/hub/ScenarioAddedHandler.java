package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.DeviceAction;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.ScenarioAddedEvent;
import ru.practicum.model.hub.ScenarioCondition;
import ru.practicum.model.hub.enums.ActionType;
import ru.practicum.model.hub.enums.ConditionOperation;
import ru.practicum.model.hub.enums.ConditionType;
import ru.practicum.model.hub.enums.HubEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Component
public class ScenarioAddedHandler extends BaseHubHandler {
    public ScenarioAddedHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {

        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEvent.getConditions().stream()
                .map(this::mapToScenarioConditionAvro)
                .toList();

        List<DeviceActionAvro> actionAvroList = scenarioAddedEvent.getActions().stream()
                .map(this::mapToDeviceActionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setActions(actionAvroList)
                .setConditions(scenarioConditionAvroList)
                .build();
    }

    private ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioCondition scenarioCondition) {

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(mapToConditionTypeAvro(scenarioCondition.getType()))
                .setValue(scenarioCondition.getValue())
                .setOperation(mapToConditionOperationAvro(scenarioCondition.getOperation()))
                .build();
    }

    private ConditionTypeAvro mapToConditionTypeAvro(ConditionType conditionType) {
        return switch (conditionType) {
            case ConditionType.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionType.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionType.SWITCH -> ConditionTypeAvro.SWITCH;
            case ConditionType.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case ConditionType.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionType.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
        };
    }

    private ConditionOperationAvro mapToConditionOperationAvro(ConditionOperation conditionOperation) {
        return switch (conditionOperation) {
            case ConditionOperation.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperation.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperation.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
        };
    }

    private DeviceActionAvro mapToDeviceActionAvro(DeviceAction deviceAction) {

        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ActionTypeAvro mapToActionTypeAvro(ActionType actionType) {
        return switch (actionType) {
            case ActionType.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionType.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionType.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionType.SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }
}
