package io.github.drednote.telegram.handler.scenario.configurer.config;

public interface ScenarioConfigConfigurerBuilder<S> {

    /**
     * Finalizes the config configuration and returns a ScenarioConfigConfigurer.
     *
     * @return a ScenarioConfigConfigurer to continue the configuration
     */
    ScenarioConfigConfigurer<S> and() throws Exception;
}
