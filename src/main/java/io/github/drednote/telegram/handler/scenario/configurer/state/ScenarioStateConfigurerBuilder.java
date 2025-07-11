package io.github.drednote.telegram.handler.scenario.configurer.state;

public interface ScenarioStateConfigurerBuilder<S> {

    /**
     * Finalizes the state configuration and returns a ScenarioStateConfigurer.
     *
     * @return a ScenarioStateConfigurer to continue the configuration
     */
    ScenarioStateConfigurer<S> and() throws Exception;
}
