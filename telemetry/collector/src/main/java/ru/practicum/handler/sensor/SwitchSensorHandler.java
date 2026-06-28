package ru.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.sensors.SwitchSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;


@Component
public class SwitchSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaTemplate<String, SensorEvent> kafkaTemplate;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        SwitchSensorEvent sensorEvent = new SwitchSensorEvent();
        sensorEvent.setId(event.getId());
        sensorEvent.setHubId(event.getHubId());
        sensorEvent.setTimestamp(toInstant(event.getTimestamp()));
        sensorEvent.setState(event.getSwitchSensor().getState());
        kafkaTemplate.send("telemetry.sensors.v1", sensorEvent.getHubId(), sensorEvent);
    }
}