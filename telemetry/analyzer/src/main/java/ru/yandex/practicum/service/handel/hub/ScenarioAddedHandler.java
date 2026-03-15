package ru.yandex.practicum.service.handel.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.model.enums.ActionType;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String scenarioName = payload.getName();

        log.info(Message.SCENARIO_ADDING,
                hubId, scenarioName,
                payload.getConditions().size(),
                payload.getActions().size());

        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(existing -> {
                    log.info(Message.SCENARIO_DELETING, existing.getId(), existing.getName());
                    scenarioRepository.delete(existing);
                    scenarioRepository.flush();
                });

        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(scenarioName)
                .build();
        scenario = scenarioRepository.save(scenario);

        saveConditions(scenario, payload.getConditions(), hubId);
        saveActions(scenario, payload.getActions(), hubId);

        log.info(Message.SCENARIO_SAVED, scenarioName, hubId);
    }

    private void saveConditions(Scenario scenario, List<ScenarioConditionAvro> conditions, String hubId) {
        for (ScenarioConditionAvro conditionAvro : conditions) {

            Sensor sensor = sensorRepository.findByIdAndHubId(conditionAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new RuntimeException(String.format(Message.SENSOR_NOT_FOUND, conditionAvro.getSensorId())));

            ConditionType type = EnumMapper.toConditionType(conditionAvro.getType());
            ConditionOperation operation = EnumMapper.toConditionOperation(conditionAvro.getOperation());
            Integer value = convertValue(conditionAvro.getValue());
            Condition condition = conditionRepository
                    .findFirstByTypeAndOperationAndValue(type.name(), operation.name(), value)
                    .orElseGet(() -> {
                        Condition newCondition = Condition.builder()
                                .type(type)
                                .operation(operation)
                                .value(value)
                                .build();
                        return conditionRepository.save(newCondition);
                    });

            ScenarioConditionId id = new ScenarioConditionId(
                    scenario.getId(),
                    sensor.getId(),
                    condition.getId()
            );

            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(id)
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();

            scenarioConditionRepository.save(scenarioCondition);

            log.debug(Message.CONDITION_ADDED,
                    sensor.getId(), condition.getType(), condition.getOperation(), condition.getValue());
        }
    }

    private Integer convertValue(Object conditionValue) {
        return switch (conditionValue) {
            case null -> 0;
            case Integer intVal -> intVal;
            case Boolean boolFlag -> boolFlag ? 1 : 0;
            default -> Integer.parseInt(conditionValue.toString());
        };
    }

    private void saveActions(Scenario scenario, List<DeviceActionAvro> actions, String hubId) {
        for (DeviceActionAvro actionAvro : actions) {

            Sensor sensor = sensorRepository.findByIdAndHubId(actionAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new RuntimeException(String.format(Message.SENSOR_NOT_FOUND, actionAvro.getSensorId())));

            ActionType type = EnumMapper.toActionType(actionAvro.getType());
            Integer value = actionAvro.getValue() != null ? actionAvro.getValue() : 0;

            Action action = actionRepository
                    .findFirstByTypeAndValueNative(type.name(), value)
                    .orElseGet(() -> {
                        Action newAction = Action.builder()
                                .type(type)
                                .value(value)
                                .build();
                        return actionRepository.save(newAction);
                    });

            ScenarioActionId id = new ScenarioActionId(
                    scenario.getId(),
                    sensor.getId(),
                    action.getId()
            );

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(id)
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();

            scenarioActionRepository.save(scenarioAction);

            log.debug(Message.ACTION_ADDED,
                    sensor.getId(), action.getType(), action.getValue());
        }
    }
}
