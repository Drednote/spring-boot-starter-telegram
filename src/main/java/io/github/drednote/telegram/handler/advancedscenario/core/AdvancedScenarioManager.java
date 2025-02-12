package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.Getter;

import java.util.*;

@Getter
public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario<?>> scenarios = new HashMap<>();
    private String currentScenarioName;

    public AdvancedScenarioManager() {
    }

    public void addScenario(String name, AdvancedScenario<?> scenario) {
        scenarios.put(name, scenario);
    }

    public AdvancedScenarioManager setCurrentScenario(String scenarioName) {
        if (!scenarios.containsKey(scenarioName)) {
            throw new IllegalArgumentException("Scenario not found: " + scenarioName);
        }
        this.currentScenarioName = scenarioName;
        return this;
    }

    public AdvancedScenario<?> getCurrentScenario() {
        return scenarios.get(currentScenarioName);
    }

    public List<AdvancedScenario<?>> getActiveScenarios() {
        if (currentScenarioName != null) {
            AdvancedScenario<?> scenario = getCurrentScenario();
            return scenario != null ? List.of(scenario) : Collections.emptyList();
        }
        return new ArrayList<>(scenarios.values());
    }
}
