package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.statemachine.config.configurers.JoinTransitionConfigurer;

public class DefaultScenarioJoinTransitionConfigurer<S>
    extends AbstractScenarioPseudoTransitionConfigurer<S>
    implements ScenarioJoinTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final JoinTransitionConfigurer<S, ScenarioEvent> configurer;

    private final List<S> sources = new ArrayList<>();

    public DefaultScenarioJoinTransitionConfigurer(
        ScenarioBuilder<S> builder,
        JoinTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioPseudoTransitionConfigurer<S> source(S source) {
        sources.add(source);
        return this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() throws Exception {
        if (target != null) {
            configurer.target(target);
        }
        for (S s : sources) {
            configurer.source(s);
        }

        return new DefaultScenarioTransitionConfigurer<>(builder, configurer.and());
    }
}
