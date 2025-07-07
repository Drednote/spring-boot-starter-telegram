package io.github.drednote.telegram.handler.scenario.configurer.transition;

public interface ScenarioTransitionConfigurerBuilder<S> {

    /**
     * Finalizes the transition configuration and returns a ScenarioTransitionConfigurer.
     * <p>
     * <b>You should always call this method after finishing configuring transition, even if
     * configured transition is last</b>
     *
     * @return a ScenarioTransitionConfigurer to continue the configuration
     */
    ScenarioTransitionConfigurer<S> and() throws Exception;
}
