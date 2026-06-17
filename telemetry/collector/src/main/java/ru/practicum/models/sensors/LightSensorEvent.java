package ru.practicum.models.sensors;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.enums.SensorEventType;

import static ru.practicum.models.enums.SensorEventType.LIGHT_SENSOR_EVENT;

@Getter
@Setter
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return LIGHT_SENSOR_EVENT;
    }
}
