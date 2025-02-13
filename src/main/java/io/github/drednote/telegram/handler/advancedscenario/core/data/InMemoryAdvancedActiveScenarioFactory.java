package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioFactory;
import org.springframework.context.ApplicationContext;

public class InMemoryAdvancedActiveScenarioFactory implements IAdvancedActiveScenarioFactory {
    private final ApplicationContext context; // Контекст для создания бина вручную

    public InMemoryAdvancedActiveScenarioFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public IAdvancedActiveScenarioEntity create(String scenarioName, Enum<?> status) {
        // Получаем бин и создаем новый объект через Spring
        return context.getBean(InMemoryAdvancedActiveScenarioEntity.class, scenarioName, status.toString());
    }
}
