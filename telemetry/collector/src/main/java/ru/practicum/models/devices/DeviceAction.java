package ru.practicum.models.devices;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.enums.ActionEnum;

@Getter
@Setter
public class DeviceAction {
    private String sensorId;
    @NotNull(message = "Тип действия не должен быть пустым")
    private ActionEnum type;
    private Integer value;
}
