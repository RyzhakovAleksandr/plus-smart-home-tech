package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.ScenarioRemovedEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.hub.HubEventAvroMapper;
import ru.practicum.service.mapper.hub.HubEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedHandler extends BaseHubHandler {

    public ScenarioRemovedHandler(KafkaEventProducer kafkaProducer,
                                  KafkaConfig kafkaConfig,
                                  HubEventAvroMapper avroMapper,
                                  HubEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageHubType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    protected HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        ScenarioRemovedEventAvro avro = avroMapper.mapScenarioRemovedToAvro((ScenarioRemovedEvent) hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        HubEvent hub = protoMapper.mapScenarioRemovedProtoToModel(hubProto.getScenarioRemoved());
        return mapBaseHubProtoFieldsToHub(hub, hubProto);
    }
}
