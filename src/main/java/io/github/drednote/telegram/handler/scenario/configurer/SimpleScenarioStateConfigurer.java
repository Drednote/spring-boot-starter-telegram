package io.github.drednote.telegram.handler.scenario.configurer;

import org.springframework.lang.Nullable;

public class SimpleScenarioStateConfigurer<S> implements ScenarioStateConfigurer<S> {

    private final ScenarioBuilder<S> scenarioBuilder;

    public SimpleScenarioStateConfigurer(ScenarioBuilder<S> scenarioBuilder) {
        this.scenarioBuilder = scenarioBuilder;
    }

    @Override
    public ScenarioStateConfigurer<S> withInitialState(S state) {
        scenarioBuilder.setInitial(state);
        return this;
    }

    @SafeVarargs
    @Override
    public final ScenarioStateConfigurer<S> withTerminalState(S state, @Nullable S... states) {
        scenarioBuilder.addTerminalState(state);
        if (states != null) {
            for (S s : states) {
                scenarioBuilder.addTerminalState(s);
            }
        }
        return this;
    }
}
