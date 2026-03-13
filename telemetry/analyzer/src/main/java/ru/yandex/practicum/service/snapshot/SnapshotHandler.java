package ru.yandex.practicum.service.snapshot;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.clientGrpc.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final HubRouterClient routerClient;

    @Transactional(readOnly = true)
    public void handle(SensorsSnapshotAvro sensorsSnapshotAvro) {
        try {
            Map<String, SensorStateAvro> sensorStateMap = sensorsSnapshotAvro.getSensorsState();
            List<Scenario> scenariosList = scenarioRepository.findByHubId(sensorsSnapshotAvro.getHubId());

            scenariosList.stream()
                    .filter(scenario -> handleScenario(scenario, sensorStateMap))
                    .forEach(scenario -> {
                        log.info(Message.INFO_ACTION_SENDING, scenario.getName());
                        sendScenarioAction(scenario);
                    });
        } catch (Exception e) {
            log.error(Message.ERROR_SNAPSHOT_PROCESSING,
                    sensorsSnapshotAvro.getHubId(), e);
            throw e;
        }

    }

    private void sendScenarioAction(Scenario scenario) {
        List<ScenarioAction> actions = scenarioActionRepository.findByScenario(scenario);
        log.info("Отправка {} действий для сценария '{}'", actions.size(), scenario.getName());
        actions.forEach(action -> {
            log.info("Действие: sensorId={}, type={}, value={}",
                    action.getSensor().getId(),
                    action.getAction().getType(),
                    action.getAction().getValue());
            routerClient.sendAction(action);
        });
    }

    private boolean handleScenario(Scenario scenario, Map<String, SensorStateAvro> sensorStateMap) {
        List<ScenarioCondition> scenarioConditions =
                scenarioConditionRepository.findByScenario(scenario);
        log.info(Message.INFO_SCENARIO_CONDITIONS_LIST,
                scenarioConditions.size(), scenario.getName());

        return scenarioConditions.stream()
                .noneMatch(sc -> !checkCondition(sc.getCondition(),
                        sc.getSensor().getId(),
                        sensorStateMap));
    }

    private boolean checkOperation(Condition condition, Integer currentValue) {
        Integer targetValue = condition.getValue();
        return switch (condition.getOperation()) {
            case EQUALS -> targetValue.equals(currentValue);
            case GREATER_THAN -> currentValue > targetValue;
            case LOWER_THAN -> currentValue < targetValue;
        };
    }

    private boolean checkCondition(Condition condition, String sensorId,
                                   Map<String, SensorStateAvro> sensorStateMap) {

        SensorStateAvro sensorState = sensorStateMap.get(sensorId);
        if (sensorState == null) {
            return false;
        }
        return switch (condition.getType()) {
            case MOTION -> {
                MotionSensorAvro motion = (MotionSensorAvro) sensorState.getData();
                yield checkOperation(condition, motion.getMotion() ? 1 : 0);
            }
            case LUMINOSITY -> {
                LightSensorAvro light = (LightSensorAvro) sensorState.getData();
                yield checkOperation(condition, light.getLuminosity());
            }
            case SWITCH -> {
                SwitchSensorAvro sw = (SwitchSensorAvro) sensorState.getData();
                yield checkOperation(condition, sw.getState() ? 1 : 0);
            }
            case TEMPERATURE -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield checkOperation(condition, climate.getTemperatureC());
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield checkOperation(condition, climate.getCo2Level());
            }
            case HUMIDITY -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield checkOperation(condition, climate.getHumidity());
            }
        };
    }
}
