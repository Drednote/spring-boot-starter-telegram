package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedScenario {
    private final String startStateName;
    @Getter
    private Map<String, AdvancedScenarioState> states = new HashMap<>();
    @Getter
    private String currentState;
    @Setter
    @Getter
    private String globalErrorTransitionState;

    public static AdvancedScenarioBuilder create(String startStateName) {
        return new AdvancedScenarioBuilder(startStateName);
    }

    public AdvancedScenario(String startStateName, Map<String, AdvancedScenarioState> states) {
        this.startStateName = startStateName;
        this.states = states;
        this.currentState = startStateName; // Begin from the start's state
    }

    public void setCurrentState(String stateName) {
        if (!states.containsKey(stateName)) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        this.currentState = stateName;
    }

    public List<TelegramRequest> getActiveConditions() {
        AdvancedScenarioState currentStateObj = states.get(currentState);
        if (currentStateObj == null) {
            throw new RuntimeException("Current state not found: " + currentState);
        }
        return currentStateObj.getConditions();
    }

    public void process(UserScenarioContext context) {
        while (!context.isEnd) {
            AdvancedScenarioState state = states.get(currentState);
            if (state == null) {
                throw new RuntimeException("State not found: " + currentState);
            }

            try {
                String nextState = state.execute(context);
                if (state.isFinal()) {
                    context.isEnd = true;
                } else {
                    currentState = nextState;
                }
            } catch (RuntimeException e) {
                if (state.getElseErrorState() != null) {
                    currentState = state.getElseErrorState();
                } else if (globalErrorTransitionState != null) {
                    currentState = globalErrorTransitionState;
                } else {
                    throw e;
                }
            }
        }
    }

    public static class AdvancedScenarioBuilder {
        private final AdvancedScenario scenario;

        public AdvancedScenarioBuilder(String startStateName) {
            this.scenario = new AdvancedScenario(startStateName, new HashMap<>());
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder state(String name) {
            return new AdvancedScenarioState.AdvancedScenarioStateBuilder(name, scenario);
        }

        public AdvancedScenarioBuilder globalErrorTransitionTo(String globalErrorState) {
            scenario.setGlobalErrorTransitionState(globalErrorState);
            return this;
        }

    }
}
