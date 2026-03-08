package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.messages.Messages;
import ru.practicum.service.handler.hub.HubEventHandler;
import ru.practicum.service.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlerMap;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlerMap;

    public EventController(Set<HubEventHandler> hubSetHandlers, Set<SensorEventHandler> sensorSetHandlers) {
        this.hubEventHandlerMap = hubSetHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageHubType,
                        Function.identity()
                ));
        this.sensorEventHandlerMap = sensorSetHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageSensorType,
                        Function.identity()
                ));
    }

    @Override
    public void collectHubEvent(HubEventProto hubProto, StreamObserver<Empty> responseObserver) {
        try {
            if (hubEventHandlerMap.containsKey(hubProto.getPayloadCase())) {
                hubEventHandlerMap.get(hubProto.getPayloadCase()).handle(hubProto);
            } else {
                throw new IllegalArgumentException(Messages.EXCEPTION_HUB_NOT_FOUND);
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto sensorProto, StreamObserver<Empty> responseObserver) {
        try {
            if (sensorEventHandlerMap.containsKey(sensorProto.getPayloadCase())) {
                sensorEventHandlerMap.get(sensorProto.getPayloadCase()).handle(sensorProto);
            } else {
                throw new IllegalArgumentException(Messages.EXCEPTION_SENSOR_NOT_FOUND);
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
