package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.sensor.SensorEventHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final SensorEventHandlerFactory sensorHandlerFactory;

    @Transactional(readOnly = true)
    public List<Scenario> analyze(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> sensorStateMap = snapshot.getSensorsState();

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        log.info("Найдено {} сценариев для хаба {}", scenarios.size(), hubId);

        return scenarios.stream()
                .filter(scenario -> checkScenario(scenario, sensorStateMap))
                .toList();
    }

    private boolean checkScenario(Scenario scenario, Map<String, SensorStateAvro> sensorStateMap) {
        List<ScenarioCondition> conditions = scenarioConditionRepository.findByScenario(scenario);
        log.debug("Проверка сценария '{}' ({} условий)", scenario.getName(), conditions.size());

        return conditions.stream()
                .allMatch(condition -> checkCondition(condition, sensorStateMap));
    }

    private boolean checkCondition(ScenarioCondition scenarioCondition,
                                   Map<String, SensorStateAvro> sensorStateMap) {

        String sensorId = scenarioCondition.getSensor().getId();
        Condition condition = scenarioCondition.getCondition();

        SensorStateAvro sensorState = sensorStateMap.get(sensorId);
        if (sensorState == null) {
            log.debug("Датчик {} не найден в снапшоте", sensorId);
            return false;
        }

        String sensorType = sensorState.getData().getClass().getName();
        SensorEventHandler handler = sensorHandlerFactory.getSensorHandlerMap().get(sensorType);

        if (handler == null) {
            log.error("Не найден обработчик для типа сенсора: {}", sensorType);
            return false;
        }

        Integer currentValue = handler.getValue(condition.getConditionType(), sensorState);
        if (currentValue == null) {
            return false;
        }

        return compareValues(currentValue, condition.getValue(), condition.getOperation());
    }

    private boolean compareValues(Integer actual, Integer expected,
                                  ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro operation) {
        return switch (operation) {
            case EQUALS -> actual.equals(expected);
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
        };
    }
}
