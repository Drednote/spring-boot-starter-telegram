package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

public interface IAdvancedActiveScenarioFactory {
    public IAdvancedActiveScenarioEntity create(String scenarioName, Enum<?> status);
}
