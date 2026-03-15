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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.service.handel.hub.HubEventHandler;

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
    HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubEventServiceImpl(Set<HubEventHandler> handlers) {
        this.hubEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    @Override
    @Transactional
    public void processEvent(HubEventAvro event) {

        String type = event.getPayload().getClass().getName();

        HubEventHandler handler = hubEventHandlers.get(type);

        if (handler == null) {
            log.warn(Message.NO_EVENT_HANDLER, type);
            return;
        }

        handler.handle(event);
    }

    @Override
    @Transactional(readOnly = true)
    public void actionExecute(List<Scenario> scenarios) {
        log.info(Message.ACTION_EXECUTE_CALLED, scenarios.size());

        if (scenarios == null || scenarios.isEmpty()) {
            log.debug(Message.NO_SCENARIOS_FOUND);
            return;
        }

        for (Scenario scenario : scenarios) {
            log.info(Message.PROCESSING_SCENARIO, scenario.getName(), scenario.getHubId());

            String hubId = scenario.getHubId();
            String name = scenario.getName();

            List<ScenarioAction> actions = scenario.getActions();

            for (ScenarioAction action : actions) {
                log.info(Message.ACTION_DETAILS,
                        action.getSensor().getId(),
                        action.getAction().getType(),
                        action.getAction().getValue());
                sendActions(action, hubId, name);
            }
        }
    }

    private void sendActions(ScenarioAction action, String hubId, String name) {
        String sensorId = action.getSensor().getId();
        String actionTypeString = action.getAction().getType().name();
        Integer value = action.getAction().getValue();

        try {
            ActionTypeProto actionTypeProto = EnumMapper.toActionTypeProto(actionTypeString);

            DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(actionTypeProto)
                    .setValue(value)
                    .build();

            DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(name)
                    .setAction(deviceActionProto)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(Instant.now().getEpochSecond())
                            .setNanos(Instant.now().getNano())
                            .build())
                    .build();

            try {
                hubRouterClient.handleDeviceAction(deviceActionRequest);
                log.info(Message.COMMAND_SENT,
                        sensorId, actionTypeString, value);
            } catch (Exception e) {
                log.error(Message.GRPC_SEND_ERROR, e.getMessage());
                throw e;
            }
        } catch (IllegalArgumentException e) {
            log.warn(Message.UNKNOWN_ACTION_TYPE,
                    actionTypeString, sensorId);
        }
    }
}
