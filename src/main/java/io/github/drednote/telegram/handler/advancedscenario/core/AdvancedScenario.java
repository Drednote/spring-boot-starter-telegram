package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedScenario<E extends Enum<E>> {
    private final E startState;
    @Getter
    private Map<E, AdvancedScenarioState<E>> states = new HashMap<>();
    @Getter
    private E currentState;
    @Setter
    @Getter
    private E globalErrorTransitionState;

    private Class<E> enumClass;

    public static <T extends Enum<T>> AdvancedScenarioBuilder<T> create(T startStateName) {
        return new AdvancedScenarioBuilder<>(startStateName);
    }

    public AdvancedScenario(E startState, Map<E, AdvancedScenarioState<E>> states) {
        this.startState = startState;
        this.states = states;
        this.currentState = startState; // Begin from the start's state
    }

    public void setCurrentState(String stateNameString) {
        E stateName = Enum.valueOf(enumClass, stateNameString);
        if (!states.containsKey(stateName)) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        this.currentState = stateName;
    }

    public List<TelegramRequest> getActiveConditions() {
        AdvancedScenarioState<E> currentStateObj = states.get(currentState);
        if (currentStateObj == null) {
            throw new RuntimeException("Current state not found: " + currentState);
        }
        return currentStateObj.getConditions();
    }

    public E process(UserScenarioContext context) {

        AdvancedScenarioState<E> state = states.get(currentState);
        if (state == null) {
            throw new RuntimeException("State not found: " + currentState);
        }

        try {
            E nextState = state.execute(context);
            if (state.isFinal()) {
                return startState;
            } else {
                return nextState;
            }
        } catch (RuntimeException e) {
            if (state.getElseErrorState() != null) {
                return state.getElseErrorState();
            } else if (globalErrorTransitionState != null) {
                return globalErrorTransitionState;
            } else {
                throw e;
            }
        }
    }

    public static class AdvancedScenarioBuilder<E extends Enum<E>> {
        private final AdvancedScenario<E> scenario;

        public AdvancedScenarioBuilder(E startStateName) {
            this.scenario = new AdvancedScenario<E>(startStateName, new HashMap<>());
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder<E> state(E stateName) {
            if (scenario.enumClass == null) scenario.enumClass = (Class<E>) stateName.getClass();
            return new AdvancedScenarioState.AdvancedScenarioStateBuilder<>(stateName, scenario);
        }

        public AdvancedScenarioBuilder<E> globalErrorTransitionTo(E globalErrorState) {
            scenario.setGlobalErrorTransitionState(globalErrorState);
            return this;
        }

    }
}
