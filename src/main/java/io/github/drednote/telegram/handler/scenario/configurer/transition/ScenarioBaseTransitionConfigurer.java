package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.guard.Guard;
import java.util.Map;
import org.springframework.statemachine.security.SecurityRule.ComparisonType;
import org.springframework.statemachine.transition.Transition;

/**
 * Interface for configuring scenario base transitions.
 *
 * @param <C> the type of the configurer
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioBaseTransitionConfigurer<C extends ScenarioBaseTransitionConfigurer<C, S>, S> {

    /**
     * Sets the source state for the transition.
     *
     * @param source the source state for the transition
     * @return the current instance of the configurer
     */
    C source(S source);

    /**
     * Specify a state this transition should belong to.
     *
     * @param state the state {@code S}
     * @return configurer for chaining
     */
    C state(S state);

    /**
     * Sets an action to be executed during the transition.
     *
     * @param action the action to be executed
     * @return the current instance of the configurer
     */
    C action(Action<S> action);

    /**
     * Specify {@link org.springframework.statemachine.action.Action} for this {@link Transition}.
     *
     * @param action the action
     * @param error action that will be called if any unexpected exception is thrown by the action.
     * @return configurer for chaining
     */
    C action(Action<S> action, Action<S> error);

    /**
     * Sets a condition that must be met for a given transition to be called. The matching is executing by
     * {@link UpdateRequestMapping}
     *
     * @param telegramRequest the TelegramRequest to set
     * @return the current instance of the configurer
     * @see UpdateRequestMapping
     */
    C telegramRequest(TelegramRequest telegramRequest);

    /**
     * Sets the additional props to be used during the transition.
     *
     * @param props additional props to pass to {@link Action} in {@link ActionContext}
     * @return the current instance of the configurer
     */
    C props(Map<String, Object> props);

    /**
     * Specify a {@link Guard} for this {@link Transition}.
     *
     * @param guard the guard
     * @return configurer for chaining
     */
    C guard(Guard<S> guard);

    /**
     * Specify a security attributes for this {@link Transition}.
     *
     * @param attributes the security attributes
     * @param match the match type
     * @return configurer for chaining
     */
    C secured(String attributes, ComparisonType match);

    /**
     * Specify a security expression for this {@link Transition}.
     *
     * @param expression the security expression
     * @return configurer for chaining
     */
    C secured(String expression);

    /**
     * Specify a name for this {@link Transition}.
     *
     * @param name the name
     * @return configurer for chaining
     */
    C name(String name);

    /**
     * Specify that this transition is triggered by a time.
     *
     * @param period timer period in millis
     * @return configurer for chaining
     */
    C timer(long period);

    /**
     * Specify that this transition is triggered once by a time after a delay.
     *
     * @param period timer period in millis
     * @return configurer for chaining
     */
    C timerOnce(long period);

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
