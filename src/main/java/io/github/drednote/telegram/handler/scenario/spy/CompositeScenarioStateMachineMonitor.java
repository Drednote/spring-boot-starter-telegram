package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.transition.Transition;

public class CompositeScenarioStateMachineMonitor<S> implements ScenarioStateMachineMonitor<S> {

    private static final Logger log = LoggerFactory.getLogger(CompositeScenarioStateMachineMonitor.class);

    private final List<ScenarioStateMachineMonitor<S>> monitors;

    public CompositeScenarioStateMachineMonitor(List<ScenarioStateMachineMonitor<S>> monitors) {
        Assert.required(monitors, "List of ScenarioStateMachineMonitor");

        this.monitors = monitors;
    }

    @Override
    public void transition(Scenario<S> scenario, Transition<S, ScenarioEvent> transition) {
        for (ScenarioStateMachineMonitor<S> monitor : monitors) {
            try {
                monitor.transition(scenario, transition);
            } catch (Exception e) {
                log.warn("Unhandled error occurred in scenario monitor", e);
            }
        }
    }
}
