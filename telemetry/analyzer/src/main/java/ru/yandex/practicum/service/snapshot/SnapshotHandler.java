package ru.yandex.practicum.service.snapshot;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.SensorEventHandlerFactory;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final SensorEventHandlerFactory sensorHandlerFactory;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendActions(List<Scenario> scenarios) {
        scenarios.forEach(this::sendScenarioAction);
    }

    private void sendScenarioAction(Scenario scenario) {
        List<ScenarioAction> actions = scenarioActionRepository.findByScenario(scenario);
        log.info("Отправка {} действий для сценария '{}'", actions.size(), scenario.getName());

        actions.forEach(action -> {
            try {
                DeviceActionRequest request = buildDeviceActionRequest(action, scenario);
                hubRouterClient.handleDeviceAction(request);
                log.info("Действие отправлено: sensorId={}, type={}, value={}",
                        action.getSensor().getId(),
                        action.getAction().getType(),
                        action.getAction().getValue());
            } catch (Exception e) {
                log.error("Ошибка отправки действия: {}", e.getMessage());
            }
        });
    }

    private DeviceActionRequest buildDeviceActionRequest(ScenarioAction scenarioAction, Scenario scenario) {
        Action action = scenarioAction.getAction();
        Sensor sensor = scenarioAction.getSensor();

        DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                .setSensorId(sensor.getId())
                .setType(mapActionTypeToProto(action.getType()))
                .setValue(action.getValue())
                .build();

        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(deviceActionProto)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();
    }

    private ActionTypeProto mapActionTypeToProto(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTION_TYPE_PROTO_ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.ACTION_TYPE_PROTO_DEACTIVATE;
            case INVERSE -> ActionTypeProto.ACTION_TYPE_PROTO_INVERSE;
            case SET_VALUE -> ActionTypeProto.ACTION_TYPE_PROTO_SET_VALUE;
        };
    }
}
