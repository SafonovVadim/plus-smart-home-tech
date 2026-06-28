package ru.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.sensors.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class TemperatureSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaTemplate<String, SensorEvent> kafkaTemplate;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorEvent sensorEvent = new TemperatureSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(toInstant(event.getTimestamp()));
        sensorEvent.setTemperatureC(event.getTemperatureSensor().getTemperatureC());
        sensorEvent.setTemperatureF(event.getTemperatureSensor().getTemperatureF());

        kafkaTemplate.send("telemetry.sensors.v1", sensorEvent.getHubId(), sensorEvent);
    }
}