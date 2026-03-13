package ru.yandex.practicum.clientGrpc;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.Sensor;

import java.time.Instant;

@Slf4j
@Service
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouter;

    public HubRouterClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hub) {
        this.hubRouter = hub;
        log.info("HubRouterClient инициализирован");
    }

    public void sendAction(ScenarioAction scenarioAction) {
        try {
            DeviceActionRequest actionRequest = mapToActionRequest(scenarioAction);
            hubRouter.handleDeviceAction(actionRequest);
            log.info(Message.INFO_ACTION_SENT,
                    scenarioAction.getScenario().getName(),
                    scenarioAction.getSensor().getId());
        } catch (Exception e) {
            log.error(Message.ERROR_ACTION_SEND, e);
        }
    }

    private ActionTypeProto mapActionTypeToProto(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTION_TYPE_PROTO_ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.ACTION_TYPE_PROTO_DEACTIVATE;
            case INVERSE -> ActionTypeProto.ACTION_TYPE_PROTO_INVERSE;
            case SET_VALUE -> ActionTypeProto.ACTION_TYPE_PROTO_SET_VALUE;
        };
    }

    private Timestamp currentTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private DeviceActionRequest mapToActionRequest(ScenarioAction scenarioAction) {
        Scenario scenario = scenarioAction.getScenario();
        Sensor sensor = scenarioAction.getSensor();
        Action action = scenarioAction.getAction();

        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(sensor.getId())
                        .setType(mapActionTypeToProto(action.getType()))
                        .setValue(action.getValue())
                        .build())
                .setTimestamp(currentTimestamp())
                .build();
    }
}