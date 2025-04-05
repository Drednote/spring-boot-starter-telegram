package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

public interface IAdvancedActiveScenarioEntity {
    String getScenarioName();

    String getStatusName();

    void setScenarioName(String scenarioName);

    void setStatusName(String statusName);
}
