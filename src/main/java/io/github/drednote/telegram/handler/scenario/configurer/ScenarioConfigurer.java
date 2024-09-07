package io.github.drednote.telegram.handler.scenario.configurer;

public interface ScenarioConfigurer<S> {

    void onConfigure(ScenarioTransitionConfigurer<S> configurer);

    void onConfigure(ScenarioConfigConfigurer<S> configurer);

    void onConfigure(ScenarioStateConfigurer<S> configurer);
}
