// telemetry/collector/src/main/java/ru/practicum/handler/hub/ScenarioRemovedEventHandler.java
package ru.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;

@Service
public class ScenarioRemovedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaClient kafkaClient;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto request) {
        kafkaClient.send("telemetry.hubs.v1", request.getHubId(), request.toByteArray());
    }
}