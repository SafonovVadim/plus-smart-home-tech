package ru.practicum.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.practicum.models.HubEvent;
import ru.practicum.models.SensorEvent;

@Service
public class ConsumerService {

    @KafkaListener(
            topics = "telemetry.hubs.v1",
            groupId = "smart-home-consumer"
    )
    public void consumeHubEvent(HubEvent event) {
        System.out.println("Получено событие хаба: " + event);
    }

    @KafkaListener(
            topics = "telemetry.sensors.v1",
            groupId = "smart-home-consumer"
    )
    public void consumeSensorEvent(SensorEvent event) {
        System.out.println("Получено событие сенсора: " + event);
    }
}
