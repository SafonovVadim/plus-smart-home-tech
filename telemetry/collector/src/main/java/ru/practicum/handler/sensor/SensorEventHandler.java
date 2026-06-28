package ru.practicum.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);

}