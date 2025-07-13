package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.configurer.transition.AdditionalScenarioConfigs;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;
import io.github.drednote.telegram.handler.scenario.guard.Guard;
import org.springframework.statemachine.transition.Transition;

/**
 * Base interface for configuring any choice transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface BaseChoiceScenarioTransitionConfigurer<S, C extends ScenarioTransitionConfigurerBuilder<S>>
    extends ScenarioTransitionConfigurerBuilder<S>, AdditionalScenarioConfigs<S, C> {

    /**
     * Specify a source state {@code S} for this {@link Transition}.
     *
     * @param source the source state {@code S}
     * @return configurer for chaining
     */
    C source(S source);

    /**
     * Specify a target state {@code S} as a first choice. This must be set.
     * <p>In normal if/else if/else this would represent if.</p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @return configurer for chaining
     */
    C first(S target, Guard<S> guard);

    /**
     * Specify a target state {@code S} as a first choice associating an {@link Action} to outgoing vertex. This must be
     * set.
     * <p>
     * In normal if/else if/else this would represent if.
     * </p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @param action the action
     * @return configurer for chaining
     */
    C first(S target, Guard<S> guard, Action<S> action);

    /**
     * Specify a target state {@code S} as a first choice associating an {@link Action} to outgoing vertex. This must be
     * set.
     * <p>
     * In normal if/else if/else this would represent if.
     * </p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @param action the action
     * @param error  action that will be called if any unexpected exception is thrown by the action.
     * @return configurer for chaining
     */
    C first(S target, Guard<S> guard, Action<S> action, Action<S> error);

    /**
     * Specify a target state {@code S} as a then choice. This is optional. Multiple thens will preserve order.
     * <p>In normal if/else if/else this would represent else if.</p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @return configurer for chaining
     */
    C then(S target, Guard<S> guard);

    /**
     * Specify a target state {@code S} as a then choice associating an {@link Action} to outgoing vertex. This is
     * optional. Multiple thens will preserve order.
     * <p>In normal if/else if/else this would represent else if.</p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @param action the action
     * @return configurer for chaining
     */
    C then(S target, Guard<S> guard, Action<S> action);

    /**
     * Specify a target state {@code S} as a then choice associating an {@link Action} to outgoing vertex. This is
     * optional. Multiple thens will preserve order.
     * <p>In normal if/else if/else this would represent else if.</p>
     *
     * @param target the target state
     * @param guard  the guard for this choice
     * @param action the action
     * @param error  action that will be called if any unexpected exception is thrown by the action.
     * @return configurer for chaining
     */
    C then(S target, Guard<S> guard, Action<S> action, Action<S> error);

    /**
     * Specify a target state {@code S} as a last choice. This must be set.
     * <p>In normal if/else if/else this would represent else.</p>
     *
     * @param target the target state
     * @return configurer for chaining
     */
    C last(S target);

    /**
     * Specify a target state {@code S} as a last choice associating an {@link Action} to outgoing vertex. This must be
     * set.
     * <p>In normal if/else if/else this would represent else.</p>
     *
     * @param target the target state
     * @param action the action
     * @return configurer for chaining
     */
    C last(S target, Action<S> action);

    /**
     * Specify a target state {@code S} as a last choice associating an {@link Action} to outgoing vertex. This must be
     * set.
     * <p>In normal if/else if/else this would represent else.</p>
     *
     * @param target the target state
     * @param action the action
     * @param error  action that will be called if any unexpected exception is thrown by the action.
     * @return configurer for chaining
     */
    C last(S target, Action<S> action, Action<S> error);
}
