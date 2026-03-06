package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.ScenarioRemovedEvent;
import ru.practicum.model.hub.enums.HubEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedHandler extends BaseHubHandler {

    public ScenarioRemovedHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public ScenarioRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }
}
