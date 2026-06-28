package ru.yandex.practicum.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();
    void handle(SensorEventProto event);
    default Instant toInstant(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}