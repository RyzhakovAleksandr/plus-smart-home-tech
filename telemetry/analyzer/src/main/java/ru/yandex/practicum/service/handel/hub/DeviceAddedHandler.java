package ru.yandex.practicum.service.handel.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();

        Sensor sensor = Sensor.builder()
                .id(payload.getId())
                .hubId(hubId)
                .build();

        sensorRepository.save(sensor);
        log.info("Устройство {} добавлено в хаб {}", payload.getId(), hubId);
    }
}
