package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EventDataProducer {

    private static final Logger log = LoggerFactory.getLogger(EventDataProducer.class);

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    // 🎲 Стартовые данные для симуляции — можно заменить на реальные данные
    private final SensorEventProto templateEvent = SensorEventProto.newBuilder()
            .setId("device-123")
            .setHubId("hub-1")
            .setTimestamp(Timestamp.newBuilder()
                    .setSeconds(Instant.now().getEpochSecond())
                    .setNanos(Instant.now().getNano()))
            .setTemperatureSensor(TemperatureSensorProto.newBuilder()
                    .setTemperatureC(20)
                    .setTemperatureF(68))
            .build();

    // 📊 Счётчик циклов для чередования типов событий
    private final AtomicInteger cycleCounter = new AtomicInteger(0);

    // 🔁 Базовый метод отправки любого события
    public void sendEvent(SensorEventProto event) {
        try {
            log.info("Отправляю данные: type={}, hubId={}, id={}",
                    event.getPayloadCase().name(), event.getHubId(), event.getId());
            Empty response = collectorStub.collectSensorEvent(event);
            log.info("✅ Получил ответ от коллектора: {}", response);
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке события в collector", e);
        }
    }

    // 🌡️ Термометр
    private SensorEventProto createTemperatureEvent() {
        int prevC = templateEvent.getTemperatureSensor().getTemperatureC();
        int newC = getRandomValue(prevC, -1, 1, -40, 80);
        int newF = (int) (newC * 1.8 + 32);

        return SensorEventProto.newBuilder(templateEvent)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .setTemperatureSensor(TemperatureSensorProto.newBuilder()
                        .setTemperatureC(newC)
                        .setTemperatureF(newF))
                .build();
    }

    // 🕶️ Датчик движения
    private SensorEventProto createMotionEvent() {
        boolean motion = Math.random() > 0.5;
        int linkQuality = 90; // фиксировано для simplicity
        int voltage = getRandomValue(2500, -50, 50, 1800, 3000);

        return SensorEventProto.newBuilder(templateEvent)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .setMotionSensor(MotionSensorProto.newBuilder()
                        .setMotion(motion)
                        .setLinkQuality(linkQuality)
                        .setVoltage(voltage))
                .build();
    }

    // 💡 Фотоэлемент
    private SensorEventProto createLightEvent() {
        int luminosity = getRandomValue(500, -50, 50, 0, 1000);
        int linkQuality = 90;

        return SensorEventProto.newBuilder(templateEvent)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .setLightSensor(LightSensorProto.newBuilder()
                        .setLuminosity(luminosity)
                        .setLinkQuality(linkQuality))
                .build();
    }

    // 🌡️ Климат
    private SensorEventProto createClimateEvent() {
        return SensorEventProto.newBuilder(templateEvent)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .setClimateSensor(ClimateSensorProto.newBuilder()
                        .setTemperatureC(getRandomValue(22, -1, 1, -40, 80))
                        .setHumidity(getRandomValue(50, -5, 5, 10, 90))
                        .setCo2Level(getRandomValue(400, -20, 20, 300, 2000)))
                .build();
    }

    // 🔌 Switch
    private SensorEventProto createSwitchEvent() {
        return SensorEventProto.newBuilder(templateEvent)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano()))
                .setSwitchSensor(SwitchSensorProto.newBuilder()
                        .setState(Math.random() > 0.5))
                .build();
    }

    // 🎲 Случайное значение
    private int getRandomValue(int prev, int deltaMin, int deltaMax, int min, int max) {
        int delta = (int) (Math.random() * (deltaMax - deltaMin + 1)) + deltaMin;
        int newValue = prev + delta;
        return Math.max(min, Math.min(max, newValue));
    }

    // ⏱️ СCHEDULED: отправляет 1 тип сенсора каждые 2 секунды, циклично
    // Всего 5 типов → каждый тип появляется раз в 10 секунд
    @Scheduled(fixedDelay = 2000) // 2 секунды
    public void scheduleSensorEvents() {
        int cycle = cycleCounter.getAndIncrement() % 5;
        SensorEventProto event;
        switch (cycle) {
            case 0:
                event = createTemperatureEvent();
                log.info("📊 [1/5] Генерирую температуру...");
                sendEvent(event);
                break;
            case 1:
                event = createMotionEvent();
                log.info("-motion [2/5] Генерирую движение...");
                sendEvent(event);
                break;
            case 2:
                event = createLightEvent();
                log.info("💡 [3/5] Генерирую освещение...");
                sendEvent(event);
                break;
            case 3:
                event = createClimateEvent();
                log.info("🌡️ [4/5] Генерирую климат...");
                sendEvent(event);
                break;
            case 4:
                event = createSwitchEvent();
                log.info("🔌 [5/5] Генерирую переключатель...");
                sendEvent(event);
                break;
            default:
                log.warn("Неожиданный cycle: {}", cycle);
        }
    }
}