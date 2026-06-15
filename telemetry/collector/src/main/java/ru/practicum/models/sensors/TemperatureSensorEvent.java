package ru.practicum.models.sensors;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.enums.SensorEventType;

import static ru.practicum.models.enums.SensorEventType.TEMPERATURE_SENSOR_EVENT;

@Getter
@Setter
public class TemperatureSensorEvent extends SensorEvent {
    private int temperatureC;
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return TEMPERATURE_SENSOR_EVENT;
    }
}
