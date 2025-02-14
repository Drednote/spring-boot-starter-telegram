package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import lombok.Getter;

import java.util.*;

@Getter
public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario<?>> scenarios = new HashMap<>();

    public AdvancedScenarioManager() {
    }

    public void addScenario(String name, AdvancedScenario<?> scenario) {
        scenario.setCurrentScenarioName(name);
        scenarios.put(name, scenario);
    }

    public AdvancedScenarioManager setUpStatesInScenarios(Optional<ArrayList<IAdvancedActiveScenarioEntity>> optionalAdvancedScenarioEntity) {
        optionalAdvancedScenarioEntity
                .ifPresent(advancedActiveScenarioEntities -> advancedActiveScenarioEntities
                        .forEach(advancedActiveScenarioEntity -> {
                            if (!scenarios.containsKey(advancedActiveScenarioEntity.getScenarioName())) {
                                throw new IllegalArgumentException("Scenario not found: " + advancedActiveScenarioEntity.getScenarioName());
                            }
                            scenarios.get(advancedActiveScenarioEntity.getScenarioName()).setCurrentState(advancedActiveScenarioEntity.getStatusName());

                        }));

        return this;
    }

    public String findScenarioName(AdvancedScenario<?> value) {
        return scenarios.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public AdvancedScenario<?> findScenarioByName(String name) {
        return scenarios.get(name);
    }

    public List<AdvancedScenario<?>> getScenarios() {
        return new ArrayList<>(scenarios.values());
    }
}
