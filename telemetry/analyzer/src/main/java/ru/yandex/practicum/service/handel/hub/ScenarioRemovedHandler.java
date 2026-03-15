package ru.yandex.practicum.service.handel.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();
        String scenarioName = payload.getName();

        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresentOrElse(scenario -> {
                            scenarioConditionRepository.deleteByScenarioId(scenario.getId());
                            scenarioActionRepository.deleteByScenarioId(scenario.getId());

                            scenarioRepository.delete(scenario);

                            log.info(Message.SCENARIO_REMOVED, scenarioName, hubId);
                        },
                        () -> log.warn(Message.SCENARIO_NOT_FOUND, scenarioName, hubId)
                );
    }
}