package ru.yandex.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class LightSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaTemplate<String, ru.yandex.practicum.SensorEvent> kafkaTemplate;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        ru.practicum.models.sensors.LightSensorEvent sensorEvent = new LightSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(toInstant(event.getTimestamp()));
        sensorEvent.setLinkQuality(event.getLightSensor().getLinkQuality());
        sensorEvent.setLuminosity(event.getLightSensor().getLuminosity());

        kafkaTemplate.send("telemetry.sensors.v1", sensorEvent.getHubId(), sensorEvent);
    }
}