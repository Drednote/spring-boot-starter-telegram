package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;

/**
 * Do not implement this interface directly, use {@link ScenarioConfigurerAdapter}
 *
 * @author Ivan Galushko
 */
public interface ScenarioConfigurer<S> {

    void onConfigure(ScenarioTransitionConfigurer<S> configurer);

    void onConfigure(ScenarioConfigConfigurer<S> configurer);

    void onConfigure(ScenarioStateConfigurer<S> configurer);
}
