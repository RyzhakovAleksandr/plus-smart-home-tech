package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.DeviceAddedEvent;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.hub.HubEventAvroMapper;
import ru.practicum.service.mapper.hub.HubEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedHandler extends BaseHubHandler {

    public DeviceAddedHandler(KafkaEventProducer kafkaProducer,
                              KafkaConfig kafkaConfig,
                              HubEventAvroMapper avroMapper,
                              HubEventProtoMapper protoMapper) {
        super(kafkaProducer, kafkaConfig, avroMapper, protoMapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageHubType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        DeviceAddedEventAvro avro = avroMapper.mapDeviceAddedToAvro((DeviceAddedEvent) hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        HubEvent hub = protoMapper.mapDeviceAddedProtoToModel(hubProto.getDeviceAdded());
        return mapBaseHubProtoFieldsToHub(hub, hubProto);
    }
}
