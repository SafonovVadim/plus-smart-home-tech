package ru.practicum.controllers;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.practicum.handler.hub.HubEventHandler;
import ru.practicum.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(
            Set<SensorEventHandler> sensorEventHandlers,
            Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getPayloadCase, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получено событие от устройства:");
            log.info("  hubId = {}", request.getId());
            log.info("  timestamp = {}", request.getTimestamp());
            log.info("  payloadCase = {}", request.getPayloadCase());


            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
            if (sensorEventHandlers.containsKey(payloadCase)) {
                sensorEventHandlers.get(payloadCase).handle(request);
                log.info("Событие успешно обработано и отправлено в Kafka: type={}", payloadCase);
            } else {
                log.error("Нет обработчика для события {}", payloadCase);
                throw new IllegalArgumentException("Не могу найти обработчик для события " + payloadCase);
            }

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при обработке события от устройства: hubId={}",
                    request.getId(), e);
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получено событие от хаба: type={}, hubId={}",
                    request.getPayloadCase(),
                    request.getHubId());

            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();
            if (hubEventHandlers.containsKey(payloadCase)) {
                hubEventHandlers.get(payloadCase).handle(request);
                log.info("Событие хаба успешно обработано и отправлено в Kafka: type={}", payloadCase);
            } else {
                log.error("Нет обработчика для события хаба {}", payloadCase);
                throw new IllegalArgumentException("Не могу найти обработчик для события " + payloadCase);
            }

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при обработке события от хаба: hubId={}", request.getHubId(), e);
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}