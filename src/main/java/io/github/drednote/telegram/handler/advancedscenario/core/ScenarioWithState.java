package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioWithState<T extends Enum<T>> {
    private T scenarioState;
    private String nextScenario;
}
