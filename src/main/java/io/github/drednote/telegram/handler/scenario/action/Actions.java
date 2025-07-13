package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;

/**
 * Utility class providing methods for executing actions and predicates with built-in error handling.
 * <p>
 * The class offers static methods to execute {@link ThrowingRunnable} and {@link ThrowingPredicate} instances,
 * capturing any exceptions thrown during execution, encapsulating them within a {@link ScenarioException}, and
 * associating the error with the provided {@link UpdateRequest}.
 *
 * @author Ivan Galushko
 */
public class Actions {

    /**
     * Executes the given {@code runnable}, handling any thrown errors by wrapping them into a {@link ScenarioException}
     * and associating the error with the provided {@link UpdateRequest}.
     *
     * @param runnable the operation to execute, which may throw exceptions
     * @param request  the update request context to record errors within
     * @throws ScenarioException if an exception occurs during runnable execution
     */
    public static void withErrorHandling(ThrowingRunnable runnable, UpdateRequest request) {
        try {
            runnable.run();
        } catch (Throwable e) {
            ScenarioException exception = new ScenarioException("During scenario action unhandled error occurred", e);
            request.getAccessor().setError(exception);
            throw exception;
        }
    }

    /**
     * Executes the given {@code runnable} predicate, returning its boolean result, and handles any errors similarly by
     * wrapping and associating with the {@link UpdateRequest}.
     *
     * @param runnable the predicate to evaluate, which may throw exceptions
     * @param request  the update request context to record errors within
     * @return the boolean result of predicate evaluation
     * @throws ScenarioException if an exception occurs during predicate evaluation
     */
    public static boolean withErrorHandling(ThrowingPredicate runnable, UpdateRequest request) {
        try {
            return runnable.evaluate();
        } catch (Throwable e) {
            ScenarioException exception = new ScenarioException("During scenario action unhandled error occurred", e);
            request.getAccessor().setError(exception);
            throw exception;
        }
    }

    /**
     * Functional interface representing an operation that can throw an exception.
     */
    public interface ThrowingRunnable {

        /**
         * Runs the operation, potentially throwing an exception.
         *
         * @throws Exception if an error occurs during execution
         */
        void run() throws Exception;
    }

    /**
     * Functional interface representing a predicate with a boolean result that can throw an exception.
     */
    public interface ThrowingPredicate {

        /**
         * Evaluates the predicate.
         *
         * @return true or false based on predicate logic
         * @throws Exception if an error occurs during evaluation
         */
        boolean evaluate() throws Exception;
    }
}
