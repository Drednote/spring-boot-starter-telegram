package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@AdvancedScenarioController(name = "SCENARIO_1_PART2", isSubScenario = true)
public class DemoScenarioSecondPart implements IAdvancedScenarioConfig {
    @Override
    public AdvancedScenario<State> getScenario() {
        return AdvancedScenario.create(State.SCENARIO_1_2PART_START)
                .globalErrorTransitionTo(State.SCENARIO_1_2PART_GLOBAL_ERROR)

                .state(State.SCENARIO_1_2PART_START)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1_PART2 state").build())
                        .on(AdvancedScenarioState.getTelegramRequest("/finish", null, null))
                        .transitionTo(State.SCENARIO_1_2PART_FINISH)

                .state(State.SCENARIO_1_2PART_GLOBAL_ERROR)
                    .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("GLOBAL ERROR HAPPENED!").build())
                        .asFinal()

                .state(State.SCENARIO_1_2PART_FINISH)
                     .execute(context -> SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("SCENARIO_1_PART2 FINISHED!").build())
                        .asFinal()

                .build();
    }
}
