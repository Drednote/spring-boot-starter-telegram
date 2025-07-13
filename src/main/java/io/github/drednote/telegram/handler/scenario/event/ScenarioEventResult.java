package io.github.drednote.telegram.handler.scenario.event;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.Scenario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateMachineEventResult;

/**
 * Represents the result of handling a scenario event.
 * <p>
 * This interface encapsulates the outcome of processing an event within a scenario, including success status, any
 * exception thrown during processing, and detailed results from the underlying state machine handling.
 * </p>
 * <p>
 * It provides an abstraction for capturing the overall outcome, facilitating further decision making or error
 * management following event processing.
 * </p>
 *
 * @param <S> the type of state in the state machine
 * @param <E> the type of event processed by the state machine
 * @author Ivan Galushko
 * @see Scenario#sendEvent(UpdateRequest)
 */
public interface ScenarioEventResult<S, E> {

    /**
     * Indicates whether the event was successfully handled.
     *
     * @return true if handling was successful; false otherwise
     */
    boolean success();

    /**
     * Returns any exception thrown during scenario processing, if present.
     *
     * @return the exception thrown during processing, or null if none occurred
     */
    @Nullable
    Exception exception();

    /**
     * Provides detailed results from the underlying state machine event executions.
     *
     * @return a list of {@link StateMachineEventResult} corresponding to each state machine sub-result
     */
    List<StateMachineEventResult<S, E>> machineResult();

    /**
     * Default implementation of {@code ScenarioEventResult} that encapsulates the outcome.
     * <p>
     * This record class provides an immutable structure with concise constructors.
     * </p>
     *
     * @param <S> the type of state
     * @param <E> the type of event
     */
    record DefaultScenarioEventResult<S, E>(
        boolean success, List<StateMachineEventResult<S, E>> machineResult,
        @Nullable Exception exception
    ) implements ScenarioEventResult<S, E> {

        /**
         * Constructs a new {@code DefaultScenarioEventResult}.
         *
         * @param success       indicates if the event was successfully handled
         * @param machineResult detailed results from state machine processing
         * @param exception     exception thrown during processing, nullable
         */
        public DefaultScenarioEventResult(
            boolean success, @Nullable List<StateMachineEventResult<S, E>> machineResult, @Nullable Exception exception
        ) {
            this.success = success;
            this.machineResult = machineResult == null ? new ArrayList<>(0) : machineResult;
            this.exception = exception;
        }
    }
}
