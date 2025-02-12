package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;

@AdvancedScenarioController(name = "demoScenario")
public class DemoScenario implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario getScenario() {
        return AdvancedScenario.create("SCENARIO_1_START")
                .state("SCENARIO_1_START")
                .on(null)
                .transitionTo("SCENARIO_1_SHOW_MENU")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_SHOW_MENU")
                .execute(ctx -> System.out.println("11"))
                .on(null)
                .transitionToScenario("SCENARIO_2") // Переход к SCENARIO_2
                .on(null)
                .transitionTo("SCENARIO_1_EXIT")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_EXIT")
                .execute(ctx -> {

                })
                .asFinal()
                .state("SCENARIO_1_ERROR")
                .execute(ctx -> System.out.println("11"))
                .on(null)
                .transitionTo("SCENARIO_1_START")
                .build();
    }
}
