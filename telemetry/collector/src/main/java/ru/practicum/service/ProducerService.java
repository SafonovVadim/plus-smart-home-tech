package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.GeneralAvroSerializer;
import ru.practicum.kafka.KafkaClient;
import ru.practicum.models.HubEvent;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.devices.DeviceAction;
import ru.practicum.models.devices.DeviceAddedEvent;
import ru.practicum.models.devices.DeviceRemovedEvent;
import ru.practicum.models.scenarios.ScenarioAddedEvent;
import ru.practicum.models.scenarios.ScenarioCondition;
import ru.practicum.models.scenarios.ScenarioRemovedEvent;
import ru.practicum.models.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import static ru.practicum.models.Topics.HUB_TOPIC;
import static ru.practicum.models.Topics.SENSOR_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {

    @Autowired
    private KafkaClient kafkaClient;

    public void sendHubEvent(HubEvent event) {
       // HubEventAvro avro = convertToHubEventAvro(event);
      //  kafkaClient.send(getTopicByEventType(event), event.getHubId(), avro);
    }

    public void sendSensorEvent(SensorEvent event) {
       // SensorEventAvro avro = convertToSensorEventAvro(event);
      //  kafkaClient.send(getTopicByEventType(event), event.getHubId(), avro);
    }

    private HubEventAvro convertToHubEventAvro(HubEvent event) {
//        HubEventAvro avro = new HubEventAvro();
//
//        avro.setHubId(event.getHubId());
//        avro.setTimestamp(event.getTimestamp());
//
//        switch (event) {
//            case DeviceAddedEvent e -> {
//                DeviceAddedEventAvro payload = new DeviceAddedEventAvro();
//                payload.setId(e.getId());
//                payload.setType(DeviceTypeAvro.valueOf(e.getDeviceType().name()));
//                avro.setPayload(payload);
//            }
//            case DeviceRemovedEvent e -> {
//                DeviceRemovedEventAvro payload = new DeviceRemovedEventAvro();
//                payload.setId(e.getId());
//                avro.setPayload(payload);
//            }
//            case ScenarioAddedEvent e -> {
//                ScenarioAddedEventAvro payload = new ScenarioAddedEventAvro();
//                payload.setName(e.getName());
//                payload.setConditions(e.getConditions().stream()
//                        .map(this::convertToScenarioConditionAvro)
//                        .toList());
//                payload.setActions(e.getActions().stream()
//                        .map(this::convertToDeviceActionAvro)
//                        .toList());
//                avro.setPayload(payload);
//            }
//            case ScenarioRemovedEvent e -> {
//                ScenarioRemovedEventAvro payload = new ScenarioRemovedEventAvro();
//                payload.setName(e.getName());
//                avro.setPayload(payload);
//            }
//            default -> log.error("Неизвестный тип события: {}", event);
//        }

        return null;
    }

    private SensorEventAvro convertToSensorEventAvro(SensorEvent event) {
        SensorEventAvro avro = new SensorEventAvro();

        avro.setId(event.getId());
        avro.setHubId(event.getHubId());
        avro.setTimestamp(event.getTimestamp());

        switch (event) {
            case ClimateSensorEvent e -> {
                ClimateSensorAvro payload = new ClimateSensorAvro();
                payload.setTemperatureC(e.getTemperatureC());
                payload.setHumidity(e.getHumidity());
                payload.setCo2Level(e.getCo2Level());
                avro.setPayload(payload);
            }
            case LightSensorEvent e -> {
                LightSensorAvro payload = new LightSensorAvro();
                payload.setLinkQuality(e.getLinkQuality());
                payload.setLuminosity(e.getLuminosity());
                avro.setPayload(payload);
            }
            case MotionSensorEvent e -> {
                MotionSensorAvro payload = new MotionSensorAvro();
                payload.setLinkQuality(e.getLinkQuality());
                payload.setMotion(e.isMotion());
                payload.setVoltage(e.getVoltage());
                avro.setPayload(payload);
            }
            case SwitchSensorEvent e -> {
                SwitchSensorAvro payload = new SwitchSensorAvro();
                payload.setState(e.isState());
                avro.setPayload(payload);
            }
            case TemperatureSensorEvent e -> {
                TemperatureSensorAvro payload = new TemperatureSensorAvro();
                payload.setId(e.getId());
                payload.setHubId(e.getHubId());
                payload.setTimestamp(e.getTimestamp());
                payload.setTemperatureC(e.getTemperatureC());
                payload.setTemperatureF(e.getTemperatureF());
                avro.setPayload(payload);
            }
            default -> log.error("Неизвестный тип события: {}", event);
        }

        return avro;
    }

//    private ScenarioConditionAvro convertToScenarioConditionAvro(ScenarioCondition cond) {
////        ScenarioConditionAvro avro = new ScenarioConditionAvro();
////        avro.setSensorId(cond.getSensorId());
////
////        String typeStr = cond.getType().name();
////        avro.setType(ConditionTypeAvro.valueOf(typeStr));
////
////        avro.setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()));
////        avro.setValue(cond.getValue());
////        return avro;
//    }

//    private DeviceActionAvro convertToDeviceActionAvro(DeviceAction action) {
////        DeviceActionAvro avro = new DeviceActionAvro();
////        avro.setSensorId(action.getSensorId());
////        avro.setType(ActionTypeAvro.valueOf(action.getType().name()));
////        avro.setValue(action.getValue());
////        return avro;
//    }

    private byte[] serialize(SpecificRecordBase record) {
        return new GeneralAvroSerializer().serialize("", record);
    }

    public static String getTopicByEventType(Object event) {
        if (event instanceof HubEvent) return HUB_TOPIC;
        if (event instanceof SensorEvent) return SENSOR_TOPIC;
        throw new IllegalArgumentException("Неизвестный тип события: " + event.getClass());
    }
}