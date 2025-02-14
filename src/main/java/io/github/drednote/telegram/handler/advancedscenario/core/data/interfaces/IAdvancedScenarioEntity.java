package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public interface IAdvancedScenarioEntity {
    Long getUserId();

    Long getChatId();

    Instant getChangeDate();

    Optional<ArrayList<IAdvancedActiveScenarioEntity>> getActiveScenarios();

    void setActiveScenarios(Optional<ArrayList<IAdvancedActiveScenarioEntity>> activeScenarios);

    Optional<String> getData();

    void setData(Optional<String> data);

    default String getKey() {
        return getUserId() + ":" + getChatId(); // Composite key
    }

    default Optional<IAdvancedActiveScenarioEntity> findActiveScenarioByName(String scenarioName) {
        return getActiveScenarios()
                .orElse(new ArrayList<>())
                .stream()
                .filter(scenario -> scenario.getScenarioName().equals(scenarioName))
                .findFirst();
    }

    default void removeActiveScenarioByName(String scenarioName) {
        getActiveScenarios().ifPresent(scenarios -> {
            scenarios.removeIf(scenario -> scenario.getScenarioName().equals(scenarioName));
            setActiveScenarios(Optional.ofNullable(scenarios.isEmpty() ? null : scenarios));
        });
    }
}
