package io.github.drednote.telegram.handler.scenario.event;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.Scenario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateMachineEventResult;

/**
 * Result of a handling event.
 *
 * @author Ivan Galushko
 * @see Scenario#sendEvent(UpdateRequest)
 */
public interface ScenarioEventResult<S, E> {

    /**
     * @return true if the event was successfully handled, false otherwise.
     */
    boolean success();

    /**
     * @return exception that thrown during scenario processing, null otherwise.
     */
    @Nullable
    Exception exception();

    List<StateMachineEventResult<S, E>> machineResult();

    /**
     * Default realization of {@code ScenarioEventResult}
     */
    record DefaultScenarioEventResult<S, E>(
        boolean success, List<StateMachineEventResult<S, E>> machineResult,
        @Nullable Exception exception
    ) implements ScenarioEventResult<S, E> {

        public DefaultScenarioEventResult(
            boolean success, @Nullable List<StateMachineEventResult<S, E>> machineResult, @Nullable Exception exception
        ) {
            this.success = success;
            this.machineResult = machineResult == null ? new ArrayList<>(0) : machineResult;
            this.exception = exception;
        }
    }
}
