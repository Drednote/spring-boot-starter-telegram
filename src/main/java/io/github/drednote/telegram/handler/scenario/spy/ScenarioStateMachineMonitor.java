package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.transition.Transition;

/**
 * Interface for monitoring the state changes within a scenario's state machine.
 * <p>
 * Implementations of this interface are notified when a transition occurs in a scenario, allowing for logging,
 * auditing, or custom actions based on state changes.
 * </p>
 *
 * @param <S> the type of state in the scenario's state machine
 * @author Ivan Galushko
 */
public interface ScenarioStateMachineMonitor<S> {

    /**
     * Called when a transition occurs within a scenario.
     *
     * @param scenario   the scenario in which the transition occurred
     * @param transition the transition object representing the change
     */
    void transition(Scenario<S> scenario, Transition<S, ScenarioEvent> transition);
}
