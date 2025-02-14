package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public interface IAdvancedActiveScenarioFactory {
    IAdvancedActiveScenarioEntity createActiveScenarioEntity(String scenarioName, Enum<?> status);

    IAdvancedScenarioEntity createScenarioEntity(Long userId, Long chatId, Instant changeDate, Optional<ArrayList<IAdvancedActiveScenarioEntity>> activeScenarios, Optional<String> data);
}
