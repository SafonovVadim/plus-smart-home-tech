package ru.yandex.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


@Service
public class DeviceAddedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaTemplate<String, ru.practicum.models.HubEvent> kafkaTemplate;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto request) {
        ru.practicum.models.devices.DeviceAddedEvent event = new ru.practicum.models.devices.DeviceAddedEvent();
        event.setHubId(request.getHubId());
        event.setId(request.getDeviceAdded().getId());
        event.setDeviceType(event.getDeviceType());
        event.setTimestamp(toInstant(request.getTimestamp()));

        kafkaTemplate.send("telemetry.hubs.v1", event.getHubId(), event);
    }
}