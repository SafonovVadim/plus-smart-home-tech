package ru.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.models.sensors.ClimateSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaTemplate<String, ru.practicum.models.SensorEvent> kafkaTemplate;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        ru.practicum.models.sensors.ClimateSensorEvent sensorEvent = new ClimateSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(toInstant(event.getTimestamp()));
        sensorEvent.setTemperatureC(event.getClimateSensor().getTemperatureC());
        sensorEvent.setHumidity(event.getClimateSensor().getHumidity());
        sensorEvent.setCo2Level(event.getClimateSensor().getCo2Level());

        kafkaTemplate.send("telemetry.sensors.v1", sensorEvent.getHubId(), sensorEvent);
    }
}