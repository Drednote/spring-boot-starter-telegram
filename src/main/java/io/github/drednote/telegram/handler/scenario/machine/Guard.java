package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.handler.scenario.ActionContext;

/**
 * @author Ivan Galushko
 */
public interface Guard<S> {

    /**
     * Evaluate a guard condition.
     *
     * @param context the state context
     * @return true, if guard evaluation is successful, false otherwise.
     */
    boolean evaluate(ActionContext<S> context);
}
