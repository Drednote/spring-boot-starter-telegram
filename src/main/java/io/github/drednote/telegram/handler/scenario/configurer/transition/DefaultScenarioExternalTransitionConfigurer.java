package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class DefaultScenarioExternalTransitionConfigurer<S>
    extends
    BaseScenarioTransitionConfigurer<ExternalTransitionConfigurer<S, ScenarioEvent>, ScenarioExternalTransitionConfigurer<S>, S>
    implements ScenarioExternalTransitionConfigurer<S> {

    private final ExternalTransitionConfigurer<S, ScenarioEvent> configurer;

    @Nullable
    private S target;

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

    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        preBuild(configurer);

        if (target != null) {
            configurer.target(target);
        }

        return configurer.and();
    }
}
