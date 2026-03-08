package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.ScenarioAddedEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.hub.HubEventAvroMapper;
import ru.practicum.service.mapper.hub.HubEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
public class ScenarioAddedHandler extends BaseHubHandler {
    public ScenarioAddedHandler(KafkaEventProducer kafkaProducer,
                                KafkaConfig kafkaConfig,
                                HubEventAvroMapper avroMapper,
                                HubEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageHubType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        ScenarioAddedEventAvro avro = avroMapper.mapScenarioAddedToAvro((ScenarioAddedEvent) hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        HubEvent hub = protoMapper.mapScenarioAddedProtoToModel(hubProto.getScenarioAdded());
        return mapBaseHubProtoFieldsToHub(hub, hubProto);
    }
}
