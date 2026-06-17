package ru.practicum.models.scenarios;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.models.enums.Operations;
import ru.practicum.models.enums.ScenarioConditionType;

@Getter
@Setter
public class ScenarioCondition {
    private String sensorId;
    @NotNull(message = "Тип условия не должен быть пустым")
    private ScenarioConditionType type;
    @NotNull(message = "Операция не должна быть пустой")
    private Operations operation;
    private Integer value;
}
