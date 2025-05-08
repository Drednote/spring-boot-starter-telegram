package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioFactory;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryAdvancedActiveScenarioFactory implements IAdvancedActiveScenarioFactory {

    public InMemoryAdvancedActiveScenarioFactory() {}

    @Override
    public IAdvancedActiveScenarioEntity createActiveScenarioEntity(String scenarioName, Enum<?> status) {
        return new InMemoryAdvancedActiveScenarioEntity(scenarioName, status.toString());
    }

    @Override
    public IAdvancedScenarioEntity createScenarioEntity(Long userId, Long chatId, Instant changeDate, List<IAdvancedActiveScenarioEntity> activeScenarios, String data) {
        return new InMemoryAdvancedScenarioEntityDTO(userId, chatId, changeDate, activeScenarios, data);
    }
}
