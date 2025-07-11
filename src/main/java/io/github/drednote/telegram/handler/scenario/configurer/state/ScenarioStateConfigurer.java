package io.github.drednote.telegram.handler.scenario.configurer.state;

/**
 * Interface for configuring scenario states.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioStateConfigurer<S> {

    /**
     * Gets a configurer for states.
     *
     * @return {@link StateConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    StateConfigurer<S> withStates() throws Exception;
}
