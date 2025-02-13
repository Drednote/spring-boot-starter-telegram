package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import lombok.Getter;

import java.util.*;

@Getter
public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario<?>> scenarios = new HashMap<>();
    private List<String> activeScenariosNames = new ArrayList<>();

    public AdvancedScenarioManager() {
    }

    public void addScenario(String name, AdvancedScenario<?> scenario) {
        scenarios.put(name, scenario);
    }

    public AdvancedScenarioManager setActiveScenarios(Optional<List<IAdvancedActiveScenarioEntity>> optionalAdvancedScenarioEntity) {
        optionalAdvancedScenarioEntity
                .ifPresent(advancedActiveScenarioEntities -> advancedActiveScenarioEntities
                        .forEach(advancedActiveScenarioEntity -> {
                            if (!scenarios.containsKey(advancedActiveScenarioEntity.getScenarioName())) {
                                throw new IllegalArgumentException("Scenario not found: " + advancedActiveScenarioEntity.getScenarioName());
                            }
                            scenarios.get(advancedActiveScenarioEntity.getScenarioName()).setCurrentState(advancedActiveScenarioEntity.getStatusName());
                            activeScenariosNames.add(advancedActiveScenarioEntity.getScenarioName());
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

    public List<AdvancedScenario<?>> getActiveScenarios() {
        if (!activeScenariosNames.isEmpty()) {
            List<AdvancedScenario<?>> activeScenarios = new ArrayList<>();
            for (String name : activeScenariosNames) {
                AdvancedScenario<?> scenario = scenarios.get(name);
                if (scenario != null) {
                    activeScenarios.add(scenario);
                }
            }
            return activeScenarios;
        } else {
            return new ArrayList<>(scenarios.values());
        }
    }
}
