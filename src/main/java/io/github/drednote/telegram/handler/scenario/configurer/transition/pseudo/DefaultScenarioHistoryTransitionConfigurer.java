package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.configurers.HistoryTransitionConfigurer;

public class DefaultScenarioHistoryTransitionConfigurer<S>
    extends AbstractScenarioPseudoTransitionConfigurer<S>
    implements ScenarioHistoryTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final HistoryTransitionConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioHistoryTransitionConfigurer(
        ScenarioBuilder<S> builder,
        HistoryTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
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
