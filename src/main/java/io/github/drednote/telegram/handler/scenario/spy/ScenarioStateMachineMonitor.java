package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.transition.Transition;

public interface ScenarioStateMachineMonitor<S> {

    /**
     * Notified when a transition is occurred.
     *
     * @param scenario   the scenario
     * @param transition the transition
     */
    void transition(Scenario<S> scenario, Transition<S, ScenarioEvent> transition);
}
