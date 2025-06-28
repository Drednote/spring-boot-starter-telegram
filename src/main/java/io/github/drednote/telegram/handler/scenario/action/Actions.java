package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;

public class Actions {

    public static void withErrorHandling(ThrowingRunnable runnable, UpdateRequest request) {
        try {
            runnable.run();
        } catch (Throwable e) {
            ScenarioException exception = new ScenarioException("During scenario action unhandled error occurred", e);
            request.getAccessor().setError(exception);
            throw exception;
        }
    }

    public static boolean withErrorHandling(ThrowingPredicate runnable, UpdateRequest request) {
        try {
            return runnable.evaluate();
        } catch (Throwable e) {
            ScenarioException exception = new ScenarioException("During scenario action unhandled error occurred", e);
            request.getAccessor().setError(exception);
            throw exception;
        }
    }

    public interface ThrowingRunnable {

        void run() throws Exception;
    }

    public interface ThrowingPredicate {

        boolean evaluate() throws Exception;
    }
}
