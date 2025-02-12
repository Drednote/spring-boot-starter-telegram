package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@AdvancedScenarioController(name = "SCENARIO_2")
public class DemoScenario2 implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario<State2> getScenario() {
        return AdvancedScenario.create(State2.SCENARIO_2_START)
                .state(State2.SCENARIO_2_START)
                .on(AdvancedScenarioState.getTelegramRequest("/hello2", null, null))
                .transitionTo(State2.SCENARIO_2_SHOW_MENU)
                .elseErrorTo(State2.SCENARIO_2_ERROR)
                .state(State2.SCENARIO_2_SHOW_MENU)
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Hello!").build())
                .on(AdvancedScenarioState.getTelegramRequest("/to_scenario_2", null, null))
                .transitionToScenario("SCENARIO_2")
                .on(null)
                .transitionTo(State2.SCENARIO_2_EXIT)
                .elseErrorTo(State2.SCENARIO_2_ERROR)
                .state(State2.SCENARIO_2_EXIT)
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Exit!").build())
                .asFinal()
                .state(State2.SCENARIO_2_ERROR)
                .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Error!").build())
                .on(null)
                .transitionTo(State2.SCENARIO_2_START)
                .build();
    }
}
