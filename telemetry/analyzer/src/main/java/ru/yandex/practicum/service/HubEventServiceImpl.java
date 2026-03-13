package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.service.hub.HubEventHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventServiceImpl implements HubEventService {

    private final Map<String, HubEventHandler> hubEventHandlers;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubEventServiceImpl(Set<HubEventHandler> handlers) {
        this.hubEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getPayloadType,
                        Function.identity()
                ));
    }

    @Override
    @Transactional
    public void processEvent(HubEventAvro event) {
        String type = event.getPayload().getClass().getSimpleName();
        HubEventHandler handler = hubEventHandlers.get(type);

        if (handler == null) {
            log.warn("Нет обработчика для типа события: {}", type);
            return;
        }

        handler.handle(event);
    }

    public void sendActions(List<Scenario> scenarios) {
        if (scenarios == null || scenarios.isEmpty()) {
            return;
        }

        for (Scenario scenario : scenarios) {
            for (ScenarioAction action : scenario.getActions()) {
                sendAction(action, scenario);
            }
        }
    }

    private void sendAction(ScenarioAction scenarioAction, Scenario scenario) {
        try {
            DeviceActionRequest request = buildDeviceActionRequest(scenarioAction, scenario);
            hubRouterClient.handleDeviceAction(request);
            log.info("Действие отправлено: sensorId={}, type={}, value={}",
                    scenarioAction.getSensor().getId(),
                    scenarioAction.getAction().getType(),
                    scenarioAction.getAction().getValue());
        } catch (Exception e) {
            log.error("Ошибка отправки действия: {}", e.getMessage());
        }
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
