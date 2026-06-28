// telemetry/collector/src/main/java/ru/practicum/handler/sensor/SwitchSensorHandler.java
package ru.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

@Component
public class SwitchSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaClient kafkaClient;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {


        kafkaClient.send("telemetry.sensors.v1", event.getId(), event.toByteArray());
    }
}