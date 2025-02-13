package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InMemoryAdvancedActiveScenarioEntity implements IAdvancedActiveScenarioEntity {
    String scenarioName;
    String statusName;
}
