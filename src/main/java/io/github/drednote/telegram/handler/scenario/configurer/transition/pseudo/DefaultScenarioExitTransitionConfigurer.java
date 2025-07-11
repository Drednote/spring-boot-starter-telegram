package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.configurers.ExitTransitionConfigurer;

public class DefaultScenarioExitTransitionConfigurer<S>
    extends AbstractScenarioPseudoTransitionConfigurer<S>
    implements ScenarioExitTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final ExitTransitionConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioExitTransitionConfigurer(
        ScenarioBuilder<S> builder,
        ExitTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() throws Exception {
        if (source != null) {
            configurer.source(source);
        }
        if (target != null) {
            configurer.target(target);
        }

        return new DefaultScenarioTransitionConfigurer<>(builder, configurer.and());
    }
}
