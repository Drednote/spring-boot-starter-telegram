package io.github.drednote.telegram.handler.scenario.guard;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;

/**
 * @author Ivan Galushko
 */
@FunctionalInterface
public interface Guard<S> {

    /**
     * Evaluate a guard condition.
     *
     * @param context the state context
     * @return true, if guard evaluation is successful, false otherwise.
     */
    boolean evaluate(ActionContext<S> context);
}
