package ru.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;

@Service
public class ScenarioAddedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaClient kafkaClient;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto request) {

        kafkaClient.send("telemetry.hubs.v1", request.getHubId(), request.toByteArray());
    }
}