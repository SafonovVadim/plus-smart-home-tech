package ru.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


@Service
public class DeviceRemovedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaTemplate<String, ru.practicum.models.HubEvent> kafkaTemplate;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto request) {
        ru.practicum.models.devices.DeviceRemovedEvent event = new ru.practicum.models.devices.DeviceRemovedEvent();
        event.setHubId(request.getHubId());
        event.setId(request.getDeviceRemoved().getId());
        event.setTimestamp(toInstant(request.getTimestamp()));

        kafkaTemplate.send("telemetry.hubs.v1", event.getHubId(), event);
    }
}