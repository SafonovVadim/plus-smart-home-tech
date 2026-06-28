package ru.practicum.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

public interface HubEventHandler {
    HubEventProto.PayloadCase getPayloadCase();
    void handle(HubEventProto request);
    default Instant toInstant(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
