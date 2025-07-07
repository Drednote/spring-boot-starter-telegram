package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.LocalTransitionConfigurer;

public class DefaultScenarioLocalTransitionConfigurer<S> extends
    BaseScenarioTransitionConfigurer<LocalTransitionConfigurer<S, ScenarioEvent>, ScenarioLocalTransitionConfigurer<S>, S>
    implements ScenarioLocalTransitionConfigurer<S> {

    private final LocalTransitionConfigurer<S, ScenarioEvent> configurer;

    @Nullable
    private S target;

    public DefaultScenarioLocalTransitionConfigurer(
        ScenarioBuilder<S> builder, LocalTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.configurer = configurer;
    }

    @Override
    public ScenarioLocalTransitionConfigurer<S> target(S target) {
        this.target = target;
        return this;
    }

    @Override
    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        preBuild(configurer);

        if (target != null) {
            configurer.target(target);
        }

        return configurer.and();
    }
}
