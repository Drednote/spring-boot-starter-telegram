package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IAdvancedActiveScenarioFactory {
    public IAdvancedActiveScenarioEntity createActiveScenarioEntity(String scenarioName, Enum<?> status);

    public IAdvancedScenarioEntity createScenarioEntity(Long userId, Long chatId, Instant changeDate, Optional<List<IAdvancedActiveScenarioEntity>> activeScenarios, Optional<String> data);
}
