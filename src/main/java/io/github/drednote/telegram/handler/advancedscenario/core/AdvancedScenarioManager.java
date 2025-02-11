package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario> scenarios = new HashMap<>();
    @Getter
    private String currentScenarioName;

    public AdvancedScenarioManager() {}

    public void addScenario(String name, AdvancedScenario scenario) {
        scenarios.put(name, scenario);
    }

    public void setCurrentScenario(String scenarioName) {
        if (!scenarios.containsKey(scenarioName)) {
            throw new IllegalArgumentException("Scenario not found: " + scenarioName);
        }
        this.currentScenarioName = scenarioName;
    }

    public AdvancedScenario getCurrentScenario() {
        return scenarios.get(currentScenarioName);
    }

    public void process(UserScenarioContext context) {
        while (!context.isEnd) {
            AdvancedScenario currentScenario = getCurrentScenario();
            if (currentScenario == null) {
                throw new RuntimeException("Current scenario not found: " + currentScenarioName);
            }

            try {
                currentScenario.process(context);

                if (context.nextScenario != null) {
                    setCurrentScenario(context.nextScenario);
                    context.nextScenario = null;
                }
            } catch (RuntimeException e) {
                throw e;
            }
        }
    }
}
