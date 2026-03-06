package ru.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.DeviceAddedEvent;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.model.hub.enums.DeviceType;
import ru.practicum.model.hub.enums.HubEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedHandler extends BaseHubHandler {

    public DeviceAddedHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(mapToDeviceTypeAvro(deviceAddedEvent.getDeviceType()))
                .build();
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceType deviceType) {
        return switch (deviceType) {
            case DeviceType.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceType.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceType.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceType.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceType.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }

}
