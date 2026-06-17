package ru.practicum.models.devices;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.HubEvent;
import ru.practicum.models.enums.DeviceAndScenarioActionType;

import static ru.practicum.models.enums.DeviceAndScenarioActionType.DEVICE_REMOVED;

@Getter
@Setter
public class DeviceRemovedEvent extends HubEvent {
    @NotNull(message = "Id устройства не должен быть пустым")
    private String id;

    @Override
    public DeviceAndScenarioActionType getType() {
        return DEVICE_REMOVED;
    }
}
