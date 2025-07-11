package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import org.springframework.lang.Nullable;

public abstract class AbstractScenarioPseudoTransitionConfigurer<S> implements ScenarioPseudoTransitionConfigurer<S> {

    @Nullable
    protected S source;
    @Nullable
    protected S target;

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
