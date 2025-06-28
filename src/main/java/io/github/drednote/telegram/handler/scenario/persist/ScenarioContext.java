package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.StateMachineContext;

/**
 * Represents a context for a scenario.
 *
 * @param <S> the type of the state in the scenario
 * @author Ivan Galushko
 */
public interface ScenarioContext<S> {

    /**
     * Retrieves the unique identifier of the scenario.
     *
     * @return a {@code String} representing the scenario's unique identifier
     */
    String getId();

    /**
     * Retrieves the state context associated with the scenario.
     *
     * @return a {@code StateContext<S>} representing the current machine context of the scenario
     */
    StateMachineContext<S, ScenarioEvent> getMachine();

    record DefaultScenarioContext<S>(
        String id, StateMachineContext<S, ScenarioEvent> machine
    ) implements ScenarioContext<S> {

        @Override
        public String getId() {
            return id;
        }

        @Override
        public StateMachineContext<S, ScenarioEvent> getMachine() {
            return machine;
        }
    }
}

