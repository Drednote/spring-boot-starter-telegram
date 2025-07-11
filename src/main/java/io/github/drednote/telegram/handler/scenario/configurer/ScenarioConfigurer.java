package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;

/**
 * Do not implement this interface directly, use {@link ScenarioConfigurerAdapter}
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioConfigurer<S> {

    /**
     * Configure transitions. Implement this method is required.
     *
     * @param configurer interface that used to configure
     */
    void onConfigure(ScenarioTransitionConfigurer<S> configurer) throws Exception;

    /**
     * Configure general setup of a scenario. Implement this method is not required.
     *
     * @param configurer interface that used to configure
     */
    void onConfigure(ScenarioConfigConfigurer<S> configurer) throws Exception;

    /**
     * Configure scenario setup. Implement this method is required.
     *
     * @param configurer interface that used to configure
     */
    void onConfigure(ScenarioStateConfigurer<S> configurer) throws Exception;
}
