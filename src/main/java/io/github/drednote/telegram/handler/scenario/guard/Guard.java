package io.github.drednote.telegram.handler.scenario.guard;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;

/**
 * Functional interface representing a guard condition used for transition control within state machines.
 * <p>
 * Implementations evaluate a condition based on the provided {@link ActionContext}, returning true if the guard
 * condition is satisfied, indicating the transition can proceed.
 * </p>
 * <p>
 * This interface facilitates flexible guard logic, supporting custom evaluations during state transitions.
 *
 * @param <S> the type of state in the state machine
 * @author Ivan Galushko
 */
@FunctionalInterface
public interface Guard<S> {

    /**
     * Evaluates the guard condition based on the given action context.
     *
     * @param context the action context provided during state transition
     * @return true if the guard condition is met; false otherwise
     */
    boolean evaluate(ActionContext<S> context);
}
