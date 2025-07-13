package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;

/**
 * An abstract adapter class for configuring scenarios. It is a main class that users should
 * implement for configuring a scenario. Information about configuring you can found on specific
 * configurators.
 *
 * @param <S> the type of the scenario state being configured
 * @author Ivan Galushko
 * @see ScenarioTransitionConfigurer
 * @see ScenarioStateConfigurer
 * @see ScenarioConfigConfigurer
 */
public abstract class ScenarioConfigurerAdapter<S> implements ScenarioConfigurer<S> {

    @Override
    public void onConfigure(ScenarioConfigConfigurer<S> configurer) throws Exception {
        // nothing
    }

    @Override
    public void onConfigure(ScenarioTransitionConfigurer<S> configurer) throws Exception {
        // nothing
    }
}
