package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioFactory;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class InMemoryAdvancedActiveScenarioFactory implements IAdvancedActiveScenarioFactory {

    public InMemoryAdvancedActiveScenarioFactory() {}

    @Override
    public IAdvancedActiveScenarioEntity createActiveScenarioEntity(String scenarioName, Enum<?> status) {
        return new InMemoryAdvancedActiveScenarioEntity(scenarioName, status.toString());
    }

    @Override
    public IAdvancedScenarioEntity createScenarioEntity(Long userId, Long chatId, Instant changeDate, Optional<List<IAdvancedActiveScenarioEntity>> activeScenarios, Optional<String> data) {
        return new InMemoryAdvancedScenarioEntityDTO(userId, chatId, changeDate, activeScenarios, data);
    }
}
