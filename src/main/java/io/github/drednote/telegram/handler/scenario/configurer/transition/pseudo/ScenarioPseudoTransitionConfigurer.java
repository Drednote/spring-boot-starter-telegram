package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;
import org.springframework.statemachine.transition.Transition;

public interface ScenarioPseudoTransitionConfigurer<S> extends ScenarioTransitionConfigurerBuilder<S> {

    /**
     * Specify a source state {@code S} for this {@link Transition}.
     *
     * @param source the source state {@code S}
     * @return configurer for chaining
     */
    ScenarioPseudoTransitionConfigurer<S> source(S source);

    /**
     * Specify a target state {@code S} for this {@link Transition}.
     *
     * @param target the target state {@code S}
     * @return configurer for chaining
     */
    ScenarioPseudoTransitionConfigurer<S> target(S target);
}
