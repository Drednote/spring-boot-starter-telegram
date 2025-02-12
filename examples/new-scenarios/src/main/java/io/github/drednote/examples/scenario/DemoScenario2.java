package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@AdvancedScenarioController(name = "SCENARIO_2")
public class DemoScenario2 implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario getScenario() {
        return AdvancedScenario.create("SCENARIO_2_START")
                .state("SCENARIO_2_START")
                .on(AdvancedScenarioState.getTelegramRequest("/hello2", null, null))
                .transitionTo("SCENARIO_2_SHOW_MENU")
                .elseErrorTo("SCENARIO_2_ERROR")
                .state("SCENARIO_2_SHOW_MENU")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Hello2!").build())
                .on(AdvancedScenarioState.getTelegramRequest("/to_scenario_1", null, null))
                .transitionToScenario("SCENARIO_1")
                .on(null)
                .transitionTo("SCENARIO_2_EXIT")
                .elseErrorTo("SCENARIO_2_ERROR")
                .state("SCENARIO_2_EXIT")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Exit2!").build())
                .asFinal()
                .state("SCENARIO_2_ERROR")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Error2!").build())
                .on(null)
                .transitionTo("SCENARIO_2_START")
                .build();
    }
}
