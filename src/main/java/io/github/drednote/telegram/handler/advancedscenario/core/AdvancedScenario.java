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

    public static <T extends Enum<T>> AdvancedScenarioBuilder<T> create(T startStateName) {
        return new AdvancedScenarioBuilder<>(startStateName);
    }

    public AdvancedScenario(E startState, Map<E, AdvancedScenarioState<E>> states) {
        this.startState = startState;
        this.states = states;
        this.currentState = startState; // Begin from the start's state
    }

    public void setCurrentState(E stateName) {
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
        while (!context.isEnd) {
            AdvancedScenarioState<E> state = states.get(currentState);
            if (state == null) {
                throw new RuntimeException("State not found: " + currentState);
            }

            try {
                E nextState = state.execute(context);
                if (state.isFinal()) {
                    context.isEnd = true;
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

        return null;
    }

    public static class AdvancedScenarioBuilder<E extends Enum<E>> {
        private final AdvancedScenario<E> scenario;

        public AdvancedScenarioBuilder(E startStateName) {
            this.scenario = new AdvancedScenario<E>(startStateName, new HashMap<>());
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder<E> state(E stateName) {
            return new AdvancedScenarioState.AdvancedScenarioStateBuilder<>(stateName, scenario);
        }

        public AdvancedScenarioBuilder<E> globalErrorTransitionTo(E globalErrorState) {
            scenario.setGlobalErrorTransitionState(globalErrorState);
            return this;
        }

    }
}
