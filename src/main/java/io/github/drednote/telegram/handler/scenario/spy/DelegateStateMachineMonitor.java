package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.function.Function;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.trigger.Trigger;
import reactor.core.publisher.Mono;

public class DelegateStateMachineMonitor<S> implements StateMachineMonitor<S, ScenarioEvent> {

    private final ScenarioStateMachineMonitor<S> delegate;

    public DelegateStateMachineMonitor(ScenarioStateMachineMonitor<S> delegate) {
        Assert.required(delegate, "ScenarioStateMachineMonitor");

        this.delegate = delegate;
    }

    @Override
    public void transition(
        StateMachine<S, ScenarioEvent> stateMachine, Transition<S, ScenarioEvent> transition, long duration
    ) {
        Trigger<S, ScenarioEvent> trigger = transition.getTrigger();
        if (trigger != null) {
            UpdateRequest request = trigger.getEvent().getUpdateRequest();
            Monitors.withErrorHandling(() -> delegate.transition(request.getScenario(), transition, duration), request);
        }
    }

    @Override
    public void action(
        StateMachine<S, ScenarioEvent> stateMachine,
        Function<StateContext<S, ScenarioEvent>, Mono<Void>> action, long duration
    ) {
        // in current moment I don't know how to handle this.
    }
}
