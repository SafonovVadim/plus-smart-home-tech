package ru.yandex.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.sensors.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class MotionSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaTemplate<String, SensorEvent> kafkaTemplate;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        ru.practicum.models.sensors.MotionSensorEvent sensorEvent = new MotionSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(toInstant(event.getTimestamp()));
        sensorEvent.setLinkQuality(event.getMotionSensor().getLinkQuality());
        sensorEvent.setMotion(event.getMotionSensor().getMotion());
        sensorEvent.setVoltage(event.getMotionSensor().getVoltage());

        kafkaTemplate.send("telemetry.sensors.v1", sensorEvent.getHubId(), sensorEvent);
    }
}