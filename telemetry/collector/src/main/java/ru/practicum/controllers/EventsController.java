package ru.practicum.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.models.HubEvent;
import ru.practicum.models.SensorEvent;
import ru.practicum.service.ProducerService;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final ProducerService producerService;

    public EventsController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        producerService.sendSensorEvent(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        producerService.sendHubEvent(event);
    }
}
