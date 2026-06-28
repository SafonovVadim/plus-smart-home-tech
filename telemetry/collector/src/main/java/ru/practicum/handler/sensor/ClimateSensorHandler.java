// telemetry/collector/src/main/java/ru/practicum/handler/sensor/ClimateSensorHandler.java
package ru.practicum.handler.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateSensorHandler implements SensorEventHandler {

    @Autowired
    private KafkaClient kafkaClient;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {

        kafkaClient.send("telemetry.sensors.v1", event.getHubId(), event.toByteArray());
    }
}