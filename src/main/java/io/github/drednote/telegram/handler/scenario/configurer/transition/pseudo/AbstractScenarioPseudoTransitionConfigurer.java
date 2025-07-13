package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import org.springframework.lang.Nullable;

public abstract class AbstractScenarioPseudoTransitionConfigurer<S> implements ScenarioPseudoTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;

    @Nullable
    protected S source;
    @Nullable
    protected S target;

    public AbstractScenarioPseudoTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public ScenarioPseudoTransitionConfigurer<S> source(S source) {
        this.source = source;
        return this;
    }

    @Override
    public ScenarioPseudoTransitionConfigurer<S> target(S target) {
        this.target = target;
        return this;
    }
}
