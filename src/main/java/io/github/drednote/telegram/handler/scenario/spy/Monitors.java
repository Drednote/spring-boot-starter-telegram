package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;

public class Monitors {

    public static void withErrorHandling(ThrowingRunnable runnable, UpdateRequest request) {
        try {
            runnable.run();
        } catch (Throwable e) {
            ScenarioException exception = new ScenarioException("During scenario monitor unhandled error occurred", e);
            request.getAccessor().setError(exception);
            throw exception;
        }
    }

    public interface ThrowingRunnable {

        void run() throws Exception;
    }
}
