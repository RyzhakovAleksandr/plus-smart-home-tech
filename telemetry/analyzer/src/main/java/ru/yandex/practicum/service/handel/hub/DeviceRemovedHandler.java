package ru.yandex.practicum.service.handel.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();

        sensorRepository.findByIdAndHubId(payload.getId(), hubId).ifPresent(sensorRepository::delete);
        log.info(Message.DEVICE_REMOVED_FROM_HUB, payload.getId(), hubId);
    }
}
