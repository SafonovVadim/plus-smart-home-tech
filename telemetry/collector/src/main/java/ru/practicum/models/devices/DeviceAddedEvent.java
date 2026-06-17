package ru.practicum.models.devices;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.HubEvent;
import ru.practicum.models.enums.DeviceAndScenarioActionType;
import ru.practicum.models.enums.DeviceType;

import static ru.practicum.models.enums.DeviceAndScenarioActionType.DEVICE_ADDED;

@Getter
@Setter
public class DeviceAddedEvent extends HubEvent {
    @NotNull(message = "Id устройства не должен быть пустым")
    private String id;

    @NotNull(message = "Тип устройства не должен быть пустым")
    private DeviceType deviceType;

    @Override
    public DeviceAndScenarioActionType getType() {
        return DEVICE_ADDED;
    }
}
