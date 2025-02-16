package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@AdvancedScenarioController(name = "SCENARIO_1")
public class DemoScenario implements IAdvancedScenarioConfig {
    private final DemoScenarioProcessor processor;

    public DemoScenario(DemoScenarioProcessor processor) {
        this.processor = processor;
    }

    @Override
    public AdvancedScenario<State> getScenario() {
        return AdvancedScenario.create(State.SCENARIO_1_START)
                .globalErrorTransitionTo(State.SCENARIO_1_GLOBAL_ERROR)

                .state(State.SCENARIO_1_START)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1_START state").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/menu", null, null))
                        .or(AdvancedScenarioState.getTelegramRequest("/options", null, null))
                        .transitionTo(State.SCENARIO_1_SHOW_MENU)
                        .elseErrorTo(State.SCENARIO_1_LOCAL1_ERROR)

                .state(State.SCENARIO_1_SHOW_MENU)
                    .execute(this.processor::sendFirstMenu)
                        .on(AdvancedScenarioState.getTelegramRequest("weather", RequestType.CALLBACK_QUERY, null))
                        .transitionTo(State.SCENARIO_1_SHOW_WEATHER)

                        .on(AdvancedScenarioState.getTelegramRequest("change_password", RequestType.CALLBACK_QUERY, null))
                        .transitionTo(State.SCENARIO_1_CHANGE_PASSWORD)

                        .on(AdvancedScenarioState.getTelegramRequest("to_sub_scenario", RequestType.CALLBACK_QUERY, null))
                        .transitionToScenario("SCENARIO_1_PART2")

                        .on(AdvancedScenarioState.getTelegramRequest("/to_error", null, null))
                        .transitionTo(State.SCENARIO_FUTURE_ERROR)
                        .elseErrorTo(State.SCENARIO_1_LOCAL1_ERROR)

                .state(State.SCENARIO_1_SHOW_WEATHER)
                    .execute(this.processor::showWeather)
                        .asFinal()

                .state(State.SCENARIO_1_CHANGE_PASSWORD)
                    .execute(this.processor::needToChangePassword)
                        .on(AdvancedScenarioState.getTelegramRequest(null, null, MessageType.TEXT))
                        .transitionTo(State.SCENARIO_1_PASS_WAS_CHANGED)
                        .elseErrorTo(State.SCENARIO_1_CHANGE_PASSWORD)
                        .conditionalTransition(data -> Optional.of(data.get("passTimes")).flatMap(times -> (int) times <= 0 ? Optional.of(true) : Optional.empty()).orElse(false), State.SCENARIO_1_CHANGE_PASSWORD_NO_POSSIBLE)

                .state(State.SCENARIO_1_CHANGE_PASSWORD_NO_POSSIBLE)
                        .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Sorry you entered your password 3 times wrong").build())
                        .asFinal()

                .state(State.SCENARIO_1_PASS_WAS_CHANGED)
                        .execute(this.processor::changePassword)
                        .asFinal()

                .state(State.SCENARIO_FUTURE_ERROR)
                    .execute(context -> {
                            throw new RuntimeException("hey");
                        })

                .state(State.SCENARIO_1_GLOBAL_ERROR)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("GLOBAL Error! "+ context.getException().getCause().getMessage()).build())
                    .asFinal()

                .state(State.SCENARIO_1_LOCAL1_ERROR)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("LOCAL1 Error!").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/to_start", null, null))
                        .transitionTo(State.SCENARIO_1_START)

                .build();
    }
}
