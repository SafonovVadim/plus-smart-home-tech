package ru.practicum.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

public interface HubEventHandler {
    HubEventProto.PayloadCase getPayloadCase();
    void handle(HubEventProto request);

}
