package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IAdvancedScenarioEntity {
    Long getUserId();

    Long getChatId();

    Instant getChangeDate();

    List<IAdvancedActiveScenarioEntity> getActiveScenarios();

    void setActiveScenarios(List<IAdvancedActiveScenarioEntity> activeScenarios);

    String getData();

    void setData(String data);

    default String getKey() {
        return getUserId() + ":" + getChatId(); // Composite key
    }

    default Optional<IAdvancedActiveScenarioEntity> findActiveScenarioByName(String scenarioName) {
        return getActiveScenarios().stream()
                .filter(scenario -> scenario.getScenarioName().equals(scenarioName))
                .findFirst();
    }

    default void removeActiveScenarioByName(String scenarioName) {
        // Get the list of active scenarios
        List<IAdvancedActiveScenarioEntity> activeScenarios = getActiveScenarios();

        // Early return if the list is null or empty
        if (activeScenarios == null || activeScenarios.isEmpty()) {
            return;
        }

        // Filter out the scenario with the matching name
        List<IAdvancedActiveScenarioEntity> updatedScenarios = activeScenarios.stream()
                .filter(scenario -> !scenario.getScenarioName().equals(scenarioName))
                .toList(); // Creates an immutable list for efficiency

        // Update the active scenarios
        setActiveScenarios(updatedScenarios.isEmpty() ? null : updatedScenarios);
    }
}
