package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;
import org.springframework.messaging.Message;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineExecutor;
import org.springframework.statemachine.support.StateMachineExecutor.StateMachineExecutorTransit;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;

public class ScenarioStateMachine<S> extends ObjectStateMachine<S, ScenarioEvent> {

    private final ScenarioStateMachineMonitor<S> scenarioStateMachineMonitor;

    public ScenarioStateMachine(
        Collection<State<S, ScenarioEvent>> states, Collection<Transition<S, ScenarioEvent>> transitions,
        State<S, ScenarioEvent> initialState, ScenarioStateMachineMonitor<S> scenarioStateMachineMonitor
    ) {
        super(states, transitions, initialState);
        this.scenarioStateMachineMonitor = scenarioStateMachineMonitor;
    }

    public ScenarioStateMachine(
        Collection<State<S, ScenarioEvent>> states, Collection<Transition<S, ScenarioEvent>> transitions,
        State<S, ScenarioEvent> initialState, Transition<S, ScenarioEvent> initialTransition,
        Message<ScenarioEvent> initialEvent, ExtendedState extendedState, UUID uuid,
        ScenarioStateMachineMonitor<S> scenarioStateMachineMonitor
    ) {
        super(states, transitions, initialState, initialTransition, initialEvent, extendedState, uuid);
        this.scenarioStateMachineMonitor = scenarioStateMachineMonitor;
    }

    @Override
    protected void onInit() throws Exception {
        super.onInit();
        StateMachineExecutorTransit<S, ScenarioEvent> delegate = getExecutorTransit();
        getStateMachineExecutor().setStateMachineExecutorTransit(
            (transition, stateContext, message) -> delegate.transit(transition, stateContext, message)
                .then(Mono.defer(() -> callMonitor(transition, message)))
        );
    }

    @SuppressWarnings("unchecked")
    private StateMachineExecutorTransit<S, ScenarioEvent> getExecutorTransit()
        throws NoSuchFieldException, IllegalAccessException {
        StateMachineExecutor<S, ScenarioEvent> executor = getStateMachineExecutor();
        Field transit = executor.getClass().getDeclaredField("stateMachineExecutorTransit");
        transit.setAccessible(true);
        return (StateMachineExecutorTransit<S, ScenarioEvent>) transit.get(executor);
    }

    private Mono<Void> callMonitor(Transition<S, ScenarioEvent> transition, Message<ScenarioEvent> message) {
        if (message != null) {
            scenarioStateMachineMonitor.transition(message.getPayload().getUpdateRequest().getScenario(), transition);
        }
        return Mono.empty();
    }
}
