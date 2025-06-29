package io.github.drednote.telegram.handler.scenario.action;

import org.springframework.lang.Nullable;

/**
 * A functional interface that represents an action to be executed with a given context during
 * transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
@FunctionalInterface
public interface Action<S> {

    /**
     * Executes the action with the specified context.
     *
     * @param context the context in which the action is executed
     * @return an optional result of the action execution, or null if there is no result
     */
    @Nullable
    Object execute(ActionContext<S> context) throws Exception;
}
