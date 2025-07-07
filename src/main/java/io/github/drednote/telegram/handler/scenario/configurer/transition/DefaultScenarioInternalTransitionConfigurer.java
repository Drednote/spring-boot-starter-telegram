package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.InternalTransitionConfigurer;

public class DefaultScenarioInternalTransitionConfigurer<S> extends
    BaseScenarioTransitionConfigurer<InternalTransitionConfigurer<S, ScenarioEvent>, ScenarioInternalTransitionConfigurer<S>, S>
    implements ScenarioInternalTransitionConfigurer<S> {

    private final InternalTransitionConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioInternalTransitionConfigurer(
        ScenarioBuilder<S> builder, InternalTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.configurer = configurer;
    }

    @Override
    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        preBuild(configurer);

        return configurer.and();
    }
}
