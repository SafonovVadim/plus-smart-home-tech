package ru.practicum.handler.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Service
public class ScenarioAddedEventHandler implements HubEventHandler {

    @Autowired
    private KafkaTemplate<String, ru.practicum.models.HubEvent> kafkaTemplate;

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto request) {
        ru.practicum.models.scenarios.ScenarioAddedEvent event = new ru.practicum.models.scenarios.ScenarioAddedEvent();
        event.setHubId(request.getHubId());
        event.setName(request.getScenarioAdded().getName());
        event.setTimestamp(toInstant(request.getTimestamp()));

        kafkaTemplate.send("telemetry.hubs.v1", event.getHubId(), event);
    }
}