package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioMainClass;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;

@AdvancedScenario(name = "demoScenario")
public class DemoScenario implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenarioMainClass getScenario() {
        return AdvancedScenarioMainClass.create("SCENARIO_1_START")
                .state("SCENARIO_1_START")
                .on(ctx -> true)
                .transitionTo("SCENARIO_1_SHOW_MENU")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_SHOW_MENU")
                .execute(ctx -> System.out.println("11"))
                .on(ctx -> true)
                .transitionToScenario("SCENARIO_2") // Переход к SCENARIO_2
                .on(ctx -> true)
                .transitionTo("SCENARIO_1_EXIT")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_EXIT")
                .execute(ctx -> {

                })
                .asFinal()
                .state("SCENARIO_1_ERROR")
                .execute(ctx -> System.out.println("11"))
                .transitionTo("SCENARIO_1_START")
                .build();
    }
}
