package ru.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.Arrays;


@Service
public class DeviceAddedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaClient kafkaClient;
    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto request) {
        kafkaClient.send("telemetry.hubs.v1", request.getHubId(), request.toByteArray());
    }
}