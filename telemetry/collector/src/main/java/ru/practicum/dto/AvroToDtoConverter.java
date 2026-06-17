package ru.practicum.dto;

import ru.practicum.models.HubEvent;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.devices.DeviceAction;
import ru.practicum.models.devices.DeviceAddedEvent;
import ru.practicum.models.devices.DeviceRemovedEvent;
import ru.practicum.models.enums.ActionEnum;
import ru.practicum.models.enums.DeviceType;
import ru.practicum.models.enums.Operations;
import ru.practicum.models.enums.ScenarioConditionType;
import ru.practicum.models.scenarios.ScenarioAddedEvent;
import ru.practicum.models.scenarios.ScenarioCondition;
import ru.practicum.models.scenarios.ScenarioRemovedEvent;
import ru.practicum.models.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.stream.Collectors;

public class AvroToDtoConverter {

    public static HubEvent toHubEvent(HubEventAvro avro) {
        if (avro == null) return null;

        Object payload = avro.getPayload();
        if (payload instanceof DeviceAddedEventAvro) {
            return toDeviceAddedEvent((DeviceAddedEventAvro) payload);
        } else if (payload instanceof DeviceRemovedEventAvro) {
            return toDeviceRemovedEvent((DeviceRemovedEventAvro) payload);
        } else if (payload instanceof ScenarioAddedEventAvro) {
            return toScenarioAddedEvent((ScenarioAddedEventAvro) payload);
        } else if (payload instanceof ScenarioRemovedEventAvro) {
            return toScenarioRemovedEvent((ScenarioRemovedEventAvro) payload);
        }

        return null;
    }

    private static DeviceAddedEvent toDeviceAddedEvent(DeviceAddedEventAvro avro) {
        DeviceAddedEvent event = new DeviceAddedEvent();
        event.setId(avro.getId());
        event.setDeviceType(toSDeviceType(avro.getType()));
        return event;
    }

    private static DeviceRemovedEvent toDeviceRemovedEvent(DeviceRemovedEventAvro avro) {
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        event.setId(avro.getId());
        return event;
    }

    private static ScenarioAddedEvent toScenarioAddedEvent(ScenarioAddedEventAvro avro) {
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        event.setName(avro.getName());
        event.setConditions(avro.getConditions().stream()
                .map(AvroToDtoConverter::toScenarioCondition)
                .collect(Collectors.toList()));
        event.setActions(avro.getActions().stream()
                .map(AvroToDtoConverter::toDeviceAction)
                .collect(Collectors.toList()));
        return event;
    }

    private static ScenarioRemovedEvent toScenarioRemovedEvent(ScenarioRemovedEventAvro avro) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        event.setName(avro.getName());
        return event;
    }

    private static ScenarioCondition toScenarioCondition(ScenarioConditionAvro avro) {
        ScenarioCondition cond = new ScenarioCondition();
        cond.setSensorId(avro.getSensorId());
        cond.setType(toScenarioConditionType(avro.getType()));

        String opStr = avro.getOperation().toString();
        cond.setOperation(Operations.valueOf(opStr.toUpperCase()));

        Object val = avro.getValue();
        cond.setValue(val instanceof Integer i ? i : null);

        return cond;
    }

    private static ScenarioConditionType toScenarioConditionType(ConditionTypeAvro avro) {
        String typeStr = avro.toString();
        return ScenarioConditionType.valueOf(typeStr);
    }

    private static DeviceType toSDeviceType(DeviceTypeAvro avro) {
        String typeStr = avro.toString();
        return DeviceType.valueOf(typeStr);
    }

    private static DeviceAction toDeviceAction(DeviceActionAvro avro) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(avro.getSensorId());

        String typeStr = avro.getType().toString();
        action.setType(ActionEnum.valueOf(typeStr.toUpperCase()));

        action.setValue(avro.getValue() instanceof Integer i ? i : null);
        return action;
    }

    public static SensorEvent toSensorEvent(SensorEventAvro avro) {
        if (avro == null) return null;

        Object payload = avro.getPayload();
        if (payload instanceof ClimateSensorAvro) {
            return toClimateSensorEvent(avro, (ClimateSensorAvro) payload);
        } else if (payload instanceof LightSensorAvro) {
            return toLightSensorEvent(avro, (LightSensorAvro) payload);
        } else if (payload instanceof MotionSensorAvro) {
            return toMotionSensorEvent(avro, (MotionSensorAvro) payload);
        } else if (payload instanceof SwitchSensorAvro) {
            return toSwitchSensorEvent(avro, (SwitchSensorAvro) payload);
        } else if (payload instanceof TemperatureSensorAvro) {
            return toTemperatureSensorEvent(avro, (TemperatureSensorAvro) payload);
        }

        return null;
    }

    private static ClimateSensorEvent toClimateSensorEvent(SensorEventAvro avro, ClimateSensorAvro payload) {
        ClimateSensorEvent event = new ClimateSensorEvent();
        event.setId(avro.getId());
        event.setHubId(avro.getHubId());
        event.setTimestamp(avro.getTimestamp());
        event.setTemperatureC(payload.getTemperatureC());
        event.setHumidity(payload.getHumidity());
        event.setCo2Level(payload.getCo2Level());
        return event;
    }

    private static LightSensorEvent toLightSensorEvent(SensorEventAvro avro, LightSensorAvro payload) {
        LightSensorEvent event = new LightSensorEvent();
        event.setId(avro.getId());
        event.setHubId(avro.getHubId());
        event.setTimestamp(avro.getTimestamp());
        event.setLinkQuality(payload.getLinkQuality());
        event.setLuminosity(payload.getLuminosity());
        return event;
    }

    private static MotionSensorEvent toMotionSensorEvent(SensorEventAvro avro, MotionSensorAvro payload) {
        MotionSensorEvent event = new MotionSensorEvent();
        event.setId(avro.getId());
        event.setHubId(avro.getHubId());
        event.setTimestamp(avro.getTimestamp());
        event.setLinkQuality(payload.getLinkQuality());
        event.setMotion(payload.getMotion());
        event.setVoltage(payload.getVoltage());
        return event;
    }

    private static SwitchSensorEvent toSwitchSensorEvent(SensorEventAvro avro, SwitchSensorAvro payload) {
        SwitchSensorEvent event = new SwitchSensorEvent();
        event.setId(avro.getId());
        event.setHubId(avro.getHubId());
        event.setTimestamp(avro.getTimestamp());
        event.setState(payload.getState());
        return event;
    }

    private static TemperatureSensorEvent toTemperatureSensorEvent(SensorEventAvro avro, TemperatureSensorAvro payload) {
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setId(avro.getId());
        event.setHubId(avro.getHubId());
        event.setTimestamp(avro.getTimestamp());
        event.setTemperatureC(payload.getTemperatureC());
        event.setTemperatureF(payload.getTemperatureF());
        return event;
    }
}