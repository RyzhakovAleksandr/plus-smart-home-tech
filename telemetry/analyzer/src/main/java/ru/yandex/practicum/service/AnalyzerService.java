package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.handel.sensor.SensorEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyzerService {

    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public AnalyzerService(ScenarioRepository scenarioRepository, Set<SensorEventHandler> handlers) {
        this.scenarioRepository = scenarioRepository;
        this.sensorEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        Function.identity()
                ));
    }

    @Transactional(readOnly = true)
    public List<Scenario> analyze(SensorsSnapshotAvro snapshot) {

        log.info(Message.ANALYZING_SNAPSHOT, snapshot);

        List<Scenario> scenariosToExecute = new ArrayList<>();

        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubIdWithActions(hubId);

        if (scenarios.isEmpty()) {
            log.debug(Message.NO_SCENARIOS_FOR_HUB, hubId);
            return scenariosToExecute;
        }

        log.info(Message.SCENARIOS_FOUND_FOR_HUB, scenarios.size(), hubId);

        for (Scenario scenario : scenarios) {
            if (checkScenario(scenario, snapshot)) {
                scenariosToExecute.add(scenario);
            }
        }

        log.debug(Message.SCENARIOS_FOUND, scenariosToExecute.size());

        return scenariosToExecute;
    }

    private boolean checkScenario(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.debug(Message.CHECKING_SCENARIO, scenario.getName());
        for (ScenarioCondition condition : scenario.getConditions()) {
            if (!checkCondition(condition, snapshot)) {
                log.debug(Message.CONDITION_NOT_MET, condition.getSensor().getId(),
                        condition.getCondition().getType());
                return false;
            }
        }
        return true;
    }

    private boolean checkCondition(ScenarioCondition condition, SensorsSnapshotAvro snapshot) {

        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();

        SensorStateAvro sensorState = sensorStates.get(condition.getSensor().getId());

        if (sensorState == null) {
            log.debug(Message.SENSOR_NOT_IN_SNAPSHOT, condition.getSensor().getId());
            return false;
        }

        String dataType = sensorState.getData().getClass().getName();

        SensorEventHandler eventHandler = sensorEventHandlers.get(dataType);

        if (eventHandler == null) {
            log.error(Message.NO_SENSOR_HANDLER, dataType);
            throw new IllegalArgumentException(String.format(Message.NO_HANDLER_FOR, dataType));
        }

        Integer actualValue = eventHandler.getValue(condition.getCondition().getType(), sensorState);

        if (actualValue == null) {
            log.debug(Message.SENSOR_VALUE_RETRIEVAL_FAILED,
                    condition.getCondition().getType(), condition.getSensor().getId());
            return false;
        }

        Integer expectedValue = condition.getCondition().getValue();
        ConditionOperation operation = condition.getCondition().getOperation();

        boolean result = compareValues(actualValue, expectedValue, operation);

        log.debug(Message.SENSOR_LOG, condition.getSensor().getId(), actualValue, expectedValue, result);

        return result;
    }

    private boolean compareValues(Integer actualValue, Integer expectedValue, ConditionOperation operation) {
        return switch (operation) {
            case LOWER_THAN -> actualValue < expectedValue;
            case EQUALS -> actualValue.equals(expectedValue);
            case GREATER_THAN -> actualValue > expectedValue;
        };
    }
}
