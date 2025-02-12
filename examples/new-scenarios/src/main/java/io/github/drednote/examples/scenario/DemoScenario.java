package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@AdvancedScenarioController(name = "SCENARIO_1")
public class DemoScenario implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario getScenario() {
        return AdvancedScenario.create("SCENARIO_1_START")
                .state("SCENARIO_1_START")
                .on(AdvancedScenarioState.getTelegramRequest("/hello", null, null))
                .transitionTo("SCENARIO_1_SHOW_MENU")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_SHOW_MENU")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Hello!").build())
                .on(AdvancedScenarioState.getTelegramRequest("/to_scenario_2", null, null))
                .transitionToScenario("SCENARIO_2")
                .on(null)
                .transitionTo("SCENARIO_1_EXIT")
                .elseErrorTo("SCENARIO_1_ERROR")
                .state("SCENARIO_1_EXIT")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Exit!").build())
                .asFinal()
                .state("SCENARIO_1_ERROR")
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Error!").build())
                .on(null)
                .transitionTo("SCENARIO_1_START")
                .build();
    }
}
