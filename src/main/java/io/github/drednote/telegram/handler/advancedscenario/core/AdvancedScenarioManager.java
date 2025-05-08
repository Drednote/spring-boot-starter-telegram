package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.Getter;

import java.util.*;

@Getter
public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario<?>> scenarios = new HashMap<>();

    public AdvancedScenarioManager() {
    }

    public void addScenario(String name, boolean isSubScenario, AdvancedScenario<?> scenario) {
        scenario.setCurrentScenarioName(name);
        scenario.setSubScenario(isSubScenario);
        scenarios.put(name, scenario);
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
