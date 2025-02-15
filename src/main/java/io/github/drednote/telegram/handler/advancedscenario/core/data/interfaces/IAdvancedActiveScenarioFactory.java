package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IAdvancedActiveScenarioFactory {
    IAdvancedActiveScenarioEntity createActiveScenarioEntity(String scenarioName, Enum<?> status);

    IAdvancedScenarioEntity createScenarioEntity(Long userId, Long chatId, Instant changeDate, List<IAdvancedActiveScenarioEntity> activeScenarios, String data);
}
