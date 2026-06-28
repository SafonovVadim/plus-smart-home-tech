package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

import java.time.Instant;

@Component
public class EventDataProducer {

    private static final Logger log = LoggerFactory.getLogger(EventDataProducer.class);

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    private void sendEvent(SensorEventProto event) {
        log.info("Отправляю данные: {}", event.getAllFields());
        Empty response = collectorStub.collectSensorEvent(event);
        log.info("Получил ответ от коллектора: {}", response);
    }

    private SensorEventProto createTemperatureSensorEvent(SensorEventProto sensor) {
        int temperatureCelsius = getRandomSensorValue(sensor.getTemperatureSensor().getTemperatureC());
        int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setTemperatureSensor(
                        TemperatureSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setTemperatureF(temperatureFahrenheit)
                                .build()
                )
                .build();
    }

    private int getRandomSensorValue(int prevValue) {
        int delta = (int) (Math.random() * 3) - 1;
        int newValue = prevValue + delta;

        return Math.max(-40, Math.min(80, newValue));
    }
}
