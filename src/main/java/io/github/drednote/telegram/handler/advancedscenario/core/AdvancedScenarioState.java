package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.*;


public class AdvancedScenarioState {
    @Getter

    private List<TelegramRequest> transitions = new ArrayList<>();
    private String defaultTransitionState;
    @Getter
    @Setter
    private String elseErrorState;
    @Setter
    private String exceptionTransitionState;
    private boolean isFinal;
    @Setter
    private Action executeAction;

    private String currentStateName;

    public AdvancedScenarioState(String currentStateName) {
        this.currentStateName = currentStateName;
    }

    public List<TelegramRequest> getConditions() {
        return transitions;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String execute(UserScenarioContext context) {
        try {
            if (executeAction != null) {
                executeAction.execute(context);
            }
            return defaultTransitionState;
        } catch (Exception e) {
            if (elseErrorState != null) {
                return elseErrorState;
            } else if (exceptionTransitionState != null) {
                return exceptionTransitionState;
            } else {
                throw new RuntimeException("Unhandled exception in state " + currentStateName, e);
            }
        }
    }

    public static class AdvancedScenarioStateBuilder {
        private final String name;
        private final Map<String, AdvancedScenarioState> states;
        private final AdvancedScenarioState state;
        private List<TelegramRequest> conditions = new ArrayList<>();
        private String transitionState;
        private AdvancedScenario advancedScenarioClass;

        public AdvancedScenarioStateBuilder(String name, AdvancedScenario advancedScenarioClass) {
            this.name = name;
            this.states = advancedScenarioClass.getStates();
            this.advancedScenarioClass = advancedScenarioClass;
            this.state = new AdvancedScenarioState(name);
            states.put(name, state);
        }

        public AdvancedScenarioStateBuilder on(TelegramRequest condition) {
            this.conditions.add(condition);
            return this;
        }

        public AdvancedScenarioStateBuilder or(TelegramRequest additionalCondition) {
            if (this.conditions.isEmpty()) {
                throw new IllegalStateException("Use .on first!");
            }
            this.conditions.add(additionalCondition);
            return this;
        }

        public AdvancedScenarioStateBuilder transitionTo(String nextState) {
            if (this.conditions.isEmpty()) {
                throw new IllegalStateException("Condition must be specified before transition.");
            }
            state.currentStateName = nextState;
            state.transitions.addAll(conditions);
            this.transitionState = nextState;
            state.defaultTransitionState = nextState;
            return this;
        }


        public AdvancedScenarioStateBuilder elseErrorTo(String errorState) {
            if (transitionState == null) {
                throw new IllegalStateException("Transition state must be specified before elseErrorTo.");
            }
            state.setElseErrorState(errorState);
            return this;
        }

        public AdvancedScenarioStateBuilder execute(Action action) {
            state.setExecuteAction(action);
            return this;
        }

        public AdvancedScenarioStateBuilder asFinal() {
            state.setFinal(true);
            return this;
        }

        public AdvancedScenarioStateBuilder transitionToScenario(String scenarioName) {
            state.setExecuteAction(ctx -> ctx.nextScenario = scenarioName);
            return this;
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder state(String name) {
            return new AdvancedScenarioState.AdvancedScenarioStateBuilder(name, advancedScenarioClass);
        }


        public AdvancedScenario build() {
            return advancedScenarioClass;
        }
    }

    @FunctionalInterface
    public interface Action {
        void execute(UserScenarioContext context);
    }

    @NotNull
    private static TelegramRequestImpl getTelegramRequest(
            @Nullable String pattern, @Nullable RequestType requestType, @Nullable MessageType messageType
    ) {
        return new TelegramRequestImpl(pattern != null ? Set.of(pattern) : Set.of(), requestType != null ? Set.of(requestType) : Set.of(),
                messageType != null ? Set.of(messageType) : Set.of(), true);
    }
}
