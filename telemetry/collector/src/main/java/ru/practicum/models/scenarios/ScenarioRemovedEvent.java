package ru.practicum.models.scenarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.HubEvent;
import ru.practicum.models.enums.DeviceAndScenarioActionType;

import static ru.practicum.models.enums.DeviceAndScenarioActionType.SCENARIO_REMOVED;

@Getter
@Setter
public class ScenarioRemovedEvent extends HubEvent {
    @NotNull(message = "Название сценария не должно быть пустым")
    @Size(min = 3, message = "Название добавленного сценария должно содержать не менее 3 символов")
    private String name;

    @Override
    public DeviceAndScenarioActionType getType() {
        return SCENARIO_REMOVED;
    }
}
