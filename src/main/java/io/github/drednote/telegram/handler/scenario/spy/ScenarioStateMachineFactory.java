package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
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

public class ScenarioStateMachineFactory<S> extends ObjectStateMachineFactory<S, ScenarioEvent> {

    private final ScenarioBuilder<S> scenarioBuilder;

    /**
     * Instantiates a new abstract state machine factory.
     *
     * @param defaultStateMachineModel the default state machine model
     * @param stateMachineModelFactory the state machine model factory
     */
    public ScenarioStateMachineFactory(
        StateMachineModel<S, ScenarioEvent> defaultStateMachineModel,
        StateMachineModelFactory<S, ScenarioEvent> stateMachineModelFactory, ScenarioBuilder<S> scenarioBuilder
    ) {
        super(defaultStateMachineModel, stateMachineModelFactory);
        this.scenarioBuilder = scenarioBuilder;
    }

    @Override
    protected StateMachine<S, ScenarioEvent> buildStateMachineInternal(
        Collection<State<S, ScenarioEvent>> states, Collection<Transition<S, ScenarioEvent>> transitions,
        State<S, ScenarioEvent> initialState, Transition<S, ScenarioEvent> initialTransition,
        Message<ScenarioEvent> initialEvent, ExtendedState extendedState, PseudoState<S, ScenarioEvent> historyState,
        Boolean contextEventsEnabled, BeanFactory beanFactory, String beanName, String machineId, UUID uuid,
        StateMachineModel<S, ScenarioEvent> stateMachineModel
    ) {
        ScenarioStateMachine<S> machine = new ScenarioStateMachine<>(
            states, transitions, initialState, initialTransition, initialEvent,
            extendedState, uuid, scenarioBuilder.getMonitor()
        );
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
