package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    Throwable exception();

    List<StateMachineEventResult<S, E>> machineResult();

    /**
     * Default realization of {@code ScenarioEventResult}
     */
    final class SimpleScenarioEventResult<S, E> implements ScenarioEventResult<S, E> {

        private final boolean success;
        private final List<StateMachineEventResult<S, E>> machineResult;
        @Nullable
        private final Throwable exception;

        public SimpleScenarioEventResult(
            boolean success, @Nullable List<StateMachineEventResult<S, E>> machineResult, @Nullable Throwable exception
        ) {
            this.success = success;
            this.machineResult = machineResult == null ? new ArrayList<>(0) : machineResult;
            this.exception = exception;
        }

        @Override
        public boolean success() {
            return success;
        }

        @Override
        public List<StateMachineEventResult<S, E>> machineResult() {
            return machineResult;
        }

        @Override
        @Nullable
        public Throwable exception() {
            return exception;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (SimpleScenarioEventResult) obj;
            return this.success == that.success &&
                   Objects.equals(this.machineResult, that.machineResult) &&
                   Objects.equals(this.exception, that.exception);
        }

        @Override
        public int hashCode() {
            return Objects.hash(success, machineResult, exception);
        }

        @Override
        public String toString() {
            return "SimpleScenarioEventResult[" +
                   "success=" + success + ", " +
                   "machineResult=" + machineResult + ", " +
                   "exception=" + exception + ']';
        }


    }
}
