package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.DeviceRemovedEvent;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.hub.HubEventAvroMapper;
import ru.practicum.service.mapper.hub.HubEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceRemoveHandler extends BaseHubHandler {

    public DeviceRemoveHandler(KafkaEventProducer kafkaProducer,
                               KafkaConfig kafkaConfig,
                               HubEventAvroMapper avroMapper,
                               HubEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageHubType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        DeviceRemovedEventAvro avro = avroMapper.mapDeviceRemoveToAvro((DeviceRemovedEvent) hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        HubEvent hub = protoMapper.mapDeviceRemovedProtoToModel(hubProto.getDeviceRemoved());
        return mapBaseHubProtoFieldsToHub(hub, hubProto);
    }
}
