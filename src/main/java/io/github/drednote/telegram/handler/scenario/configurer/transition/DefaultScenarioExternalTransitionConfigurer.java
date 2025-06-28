package io.github.drednote.telegram.handler.scenario.configurer.transition;

import static io.github.drednote.telegram.handler.scenario.DefaultScenario.RESPONSE_PROCESSING_PROPERTY;

import io.github.drednote.telegram.handler.scenario.action.ScenarioPropertiesAction;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.Map;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class DefaultScenarioExternalTransitionConfigurer<S>
    extends
    AbstractScenarioBaseTransitionConfigurer<ExternalTransitionConfigurer<S, ScenarioEvent>, ScenarioExternalTransitionConfigurer<S>, S>
    implements ScenarioExternalTransitionConfigurer<S> {

    private final ExternalTransitionConfigurer<S, ScenarioEvent> configurer;

    private boolean inlineKeyboard = false;

    public DefaultScenarioExternalTransitionConfigurer(
        ScenarioBuilder<S> builder,
        ExternalTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.configurer = configurer;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> target(S target) {
        this.target = target;
        return this;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> inlineKeyboardCreation() {
        this.inlineKeyboard = true;
        return this;
    }

    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        if (inlineKeyboard) {
            configurer.action(new ScenarioPropertiesAction<>(Map.of(RESPONSE_PROCESSING_PROPERTY, true)));
        }

        preBuild(configurer);

        if (target != null) {
            configurer.target(target);
        }

        return configurer.and();
    }
}
