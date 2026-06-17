package ru.practicum.models.sensors;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.enums.SensorEventType;

import static ru.practicum.models.enums.SensorEventType.CLIMATE_SENSOR_EVENT;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {
    private int temperatureC;
    private int humidity;
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return CLIMATE_SENSOR_EVENT;
    }
}
