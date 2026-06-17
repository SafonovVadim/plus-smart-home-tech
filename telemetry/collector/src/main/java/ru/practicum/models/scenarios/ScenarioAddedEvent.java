package ru.practicum.models.scenarios;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.HubEvent;
import ru.practicum.models.devices.DeviceAction;
import ru.practicum.models.enums.DeviceAndScenarioActionType;

import java.util.List;

import static ru.practicum.models.enums.DeviceAndScenarioActionType.SCENARIO_ADDED;

@Getter
@Setter
public class ScenarioAddedEvent extends HubEvent {
    @NotNull(message = "Название сценария не должно быть пустым")
    @Size(min = 3, message = "Название добавленного сценария должно содержать не менее 3 символов")
    private String name;

    @Valid
    @NotEmpty(message = "Условия сценария не должны быть пустыми")
    private List<ScenarioCondition> conditions;

    @Valid
    @NotEmpty(message = "Действия сценария не должны быть пустыми")
    private List<DeviceAction> actions;

    @Override
    public DeviceAndScenarioActionType getType() {
        return SCENARIO_ADDED;
    }
}
