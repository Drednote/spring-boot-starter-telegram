package io.github.drednote.telegram.handler.scenario.configurer;

public interface ScenarioTransitionConfigurer<S> {

    ScenarioExternalTransitionConfigurer<S> withExternal();
}
