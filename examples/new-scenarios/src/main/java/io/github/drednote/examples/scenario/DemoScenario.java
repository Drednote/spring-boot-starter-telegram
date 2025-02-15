package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Objects;

@AdvancedScenarioController(name = "SCENARIO_1")
public class DemoScenario implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario<State> getScenario() {
        return AdvancedScenario.create(State.SCENARIO_1_START)
                .globalErrorTransitionTo(State.SCENARIO_1_GLOBAL_ERROR)

                .state(State.SCENARIO_1_START)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1_START state").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/hello", null, null))
                        .transitionTo(State.SCENARIO_1_SHOW_MENU)
                        .elseErrorTo(State.SCENARIO_1_LOCAL1_ERROR)

                .state(State.SCENARIO_1_SHOW_MENU)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1_SHOW_MENU state").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/to_scenario_2", null, null))
                        .transitionToScenario("SCENARIO_2")

                        .on(AdvancedScenarioState.getTelegramRequest("/exit", null, null))
                        .transitionTo(State.SCENARIO_1_EXIT)

                        .on(AdvancedScenarioState.getTelegramRequest("/to_error", null, null))
                        .transitionTo(State.SCENARIO_FUTURE_ERROR)
                        .elseErrorTo(State.SCENARIO_1_LOCAL1_ERROR)

                        .on(AdvancedScenarioState.getTelegramRequest("/conditional", null, null))
                        .transitionTo(State.SCENARIO_1_START)
                        .conditionalTransition(Objects::nonNull, State.SCENARIO_1_LOCAL1_ERROR)

                .state(State.SCENARIO_FUTURE_ERROR)
                    .execute(context -> {
                            throw new RuntimeException("hey");
                        })

                .state(State.SCENARIO_1_EXIT)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1 Exit!").build())
                    .asFinal()

                .state(State.SCENARIO_1_GLOBAL_ERROR)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("GLOBAL Error!").build())
                    .asFinal()

                .state(State.SCENARIO_1_LOCAL1_ERROR)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("LOCAL1 Error!").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/to_start", null, null))
                        .transitionTo(State.SCENARIO_1_START)

                .build();
    }
}
