package ru.practicum.models.sensors;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.enums.SensorEventType;

import static ru.practicum.models.enums.SensorEventType.SWITCH_SENSOR_EVENT;

@Getter
@Setter
public class SwitchSensorEvent extends SensorEvent {
    private boolean state;

    @Override
    public SensorEventType getType() {
        return SWITCH_SENSOR_EVENT;
    }
}
