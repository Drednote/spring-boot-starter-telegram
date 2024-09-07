package io.github.drednote.telegram.handler.scenario.configurer;

import org.springframework.lang.Nullable;

public interface ScenarioStateConfigurer<S> {

    ScenarioStateConfigurer<S> withInitialState(S state);

    ScenarioStateConfigurer<S> withTerminalState(S state, @Nullable S... states);
}
