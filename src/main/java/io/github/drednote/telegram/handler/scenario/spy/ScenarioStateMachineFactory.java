package io.github.drednote.telegram.handler.scenario.spy;

import java.util.Collection;
import java.util.UUID;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.messaging.Message;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.config.ObjectStateMachineFactory;
import org.springframework.statemachine.config.model.StateMachineModel;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.state.PseudoState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

public class ScenarioStateMachineFactory<S, E> extends ObjectStateMachineFactory<S, E> {

    /**
     * Instantiates a new abstract state machine factory.
     *
     * @param defaultStateMachineModel the default state machine model
     * @param stateMachineModelFactory the state machine model factory
     */
    public ScenarioStateMachineFactory(StateMachineModel<S, E> defaultStateMachineModel,
        StateMachineModelFactory<S, E> stateMachineModelFactory) {
        super(defaultStateMachineModel, stateMachineModelFactory);
    }

    @Override
    protected StateMachine<S, E> buildStateMachineInternal(Collection<State<S, E>> states,
        Collection<Transition<S, E>> transitions, State<S, E> initialState, Transition<S, E> initialTransition,
        Message<E> initialEvent, ExtendedState extendedState, PseudoState<S, E> historyState,
        Boolean contextEventsEnabled, BeanFactory beanFactory, String beanName, String machineId, UUID uuid,
        StateMachineModel<S, E> stateMachineModel) {
        ScenarioStateMachine<S, E> machine = new ScenarioStateMachine<>(states, transitions, initialState,
            initialTransition, initialEvent,
            extendedState, uuid);
        machine.setId(machineId);
        machine.setHistoryState(historyState);
        machine.setTransitionConflightPolicy(stateMachineModel.getConfigurationData().getTransitionConflictPolicy());
        if (contextEventsEnabled != null) {
            machine.setContextEventsEnabled(contextEventsEnabled);
        }
        if (beanFactory != null) {
            machine.setBeanFactory(beanFactory);
        }
        if (machine instanceof BeanNameAware) {
            //When using StateMachineFactory.getStateMachine() to generate state machine,
            //which means name and id are null
            //in that case set name to the default `stateMachine`
            if ((machineId == null || machineId.isEmpty()) && (beanName == null || beanName.isEmpty())) {
                beanName = StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE;
            }
            ((BeanNameAware) machine).setBeanName(beanName);
        }
        return machine;
    }
}
