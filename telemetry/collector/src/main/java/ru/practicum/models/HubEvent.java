package ru.practicum.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.models.devices.DeviceAddedEvent;
import ru.practicum.models.devices.DeviceRemovedEvent;
import ru.practicum.models.enums.DeviceAndScenarioActionType;
import ru.practicum.models.scenarios.ScenarioAddedEvent;
import ru.practicum.models.scenarios.ScenarioRemovedEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = DeviceAndScenarioActionType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Getter
@Setter
@ToString
public abstract class HubEvent {
    @NotNull(message = "HubId не должен быть пустым")
    private String hubId;
    private Instant timestamp = Instant.now();

    @NotNull
    public abstract DeviceAndScenarioActionType getType();
}
