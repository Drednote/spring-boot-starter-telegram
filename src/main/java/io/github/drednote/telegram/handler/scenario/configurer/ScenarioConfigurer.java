package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;

/**
 * Interface for configuring scenarios, including transition rules and general setup.
 * <p>
 * This interface defines methods that must or can be implemented to customize various aspects of a scenario's
 * configuration, such as transitions and general setup.
 *
 * @param <S> the type of state managed by the scenario
 * @author Ivan Galushko
 */
public interface ScenarioConfigurer<S> {

    /**
     * Configure the transitions for the scenario.
     * <p>
     * This method is optional. Implementations should define transition rules through the provided configurer.
     * </p>
     *
     * @param configurer an interface used for configuring transitions
     * @throws Exception if an error occurs during configuration
     */
    void onConfigure(ScenarioTransitionConfigurer<S> configurer) throws Exception;

    /**
     * Configure general setup of the scenario.
     * <p>
     * This method is optional. It allows for configuring general parameters or behaviors for the scenario, such as
     * properties or default settings.
     * </p>
     *
     * @param configurer an interface used for general scenario configuration
     * @throws Exception if an error occurs during configuration
     */
    void onConfigure(ScenarioConfigConfigurer<S> configurer) throws Exception;

    /**
     * Configure scenario-specific setup like predefined states or initial conditions.
     * <p>
     * This method is required and should include configuration of scenario setup details.
     * </p>
     *
     * @param configurer an interface used for scenario setup configuration
     * @throws Exception if an error occurs during configuration
     */
    void onConfigure(ScenarioStateConfigurer<S> configurer) throws Exception;
}
