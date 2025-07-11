package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;
import org.springframework.messaging.Message;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.state.PseudoStateKind;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineExecutor;
import org.springframework.statemachine.support.StateMachineExecutor.StateMachineExecutorTransit;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.transition.TransitionKind;
import reactor.core.publisher.Mono;

public class ScenarioStateMachine<S, E> extends ObjectStateMachine<S, E> {

//    private final ScenarioStateMachineMonitor<S> scenarioStateMachineMonitor;

    public ScenarioStateMachine(Collection<State<S, E>> states, Collection<Transition<S, E>> transitions,
        State<S, E> initialState) {
        super(states, transitions, initialState);
    }

    public ScenarioStateMachine(Collection<State<S, E>> states, Collection<Transition<S, E>> transitions,
        State<S, E> initialState, Transition<S, E> initialTransition, Message<E> initialEvent,
        ExtendedState extendedState, UUID uuid) {
        super(states, transitions, initialState, initialTransition, initialEvent, extendedState, uuid);
    }

//    @Override
//    protected void onInit() throws Exception {
//        super.onInit();
//        StateMachineExecutor<S, E> executor = getStateMachineExecutor();
//        Field transit = executor.getClass().getDeclaredField("stateMachineExecutorTransit");
//        transit.setAccessible(true);
//        StateMachineExecutorTransit<S, E> delegate = (StateMachineExecutorTransit<S, E>) transit.get(executor);
//        getStateMachineExecutor().setStateMachineExecutorTransit(
//            (transition, stateContext, message) -> delegate.transit(transition, stateContext, message)
//                .then(Mono.defer(() -> callMonitor(transition, stateContext, message)))
//        );
//    }

//    private Mono<Void> callMonitor(
//        Transition<S, ScenarioEvent> transition, StateContext<S, ScenarioEvent> stateContext,
//        Message<ScenarioEvent> message
//    ) {
//        if (message != null) {
//            scenarioStateMachineMonitor.transition(message.getPayload().getUpdateRequest().getScenario(), transition);
//        }
//        return Mono.empty();
//    }
}
