package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvancedScenarioState {
    @Getter
    private final String name;
    private final Map<Condition, String> transitions = new HashMap<>();
    private String defaultTransition;
    @Getter
    @Setter
    private String elseErrorState;
    @Setter
    private String exceptionTransitionState;
    private boolean isFinal;
    @Setter
    private Action executeAction;

    public AdvancedScenarioState(String name) {
        this.name = name;
    }

    public List<Condition> getConditions() {
        return new ArrayList<>(transitions.keySet());
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

            for (Map.Entry<Condition, String> entry : transitions.entrySet()) {
                if (entry.getKey().test(context)) {
                    return entry.getValue();
                }
            }

            if (elseErrorState != null) {
                return elseErrorState;
            }

            return defaultTransition;
        } catch (Exception e) {
            if (exceptionTransitionState != null) {
                return exceptionTransitionState;
            } else {
                throw new RuntimeException("Unhandled exception in state " + name, e);
            }
        }
    }

    public static class AdvancedScenarioStateBuilder {
        private final String name;
        private final Map<String, AdvancedScenarioState> states;
        private final AdvancedScenarioState state;
        private Condition condition;
        private String transitionState;
        private AdvancedScenario advancedScenarioClass;

        public AdvancedScenarioStateBuilder(String name, AdvancedScenario advancedScenarioClass) {
            this.name = name;
            this.states = advancedScenarioClass.getStates();
            this.advancedScenarioClass = advancedScenarioClass;
            this.state = new AdvancedScenarioState(name);
            states.put(name, state);
        }

        public AdvancedScenarioStateBuilder on(Condition condition) {
            this.condition = condition;
            return this;
        }

        public AdvancedScenarioStateBuilder or(Condition additionalCondition) {
            if (this.condition == null) {
                this.condition = additionalCondition;
            } else {
                this.condition = combineConditions(this.condition, additionalCondition);
            }
            return this;
        }

        public AdvancedScenarioStateBuilder transitionTo(String nextState) {
            state.transitions.put(condition, nextState);
            this.transitionState = nextState;
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


        private static Condition combineConditions(Condition... conditions) {
            return ctx -> {
                for (Condition condition : conditions) {
                    if (condition.test(ctx)) {
                        return true;
                    }
                }
                return false;
            };
        }


        public AdvancedScenario build() {
            return advancedScenarioClass;
        }
    }

    @FunctionalInterface
    public interface Condition {
        boolean test(UserScenarioContext context);
    }

    @FunctionalInterface
    public interface Action {
        void execute(UserScenarioContext context);
    }
}
