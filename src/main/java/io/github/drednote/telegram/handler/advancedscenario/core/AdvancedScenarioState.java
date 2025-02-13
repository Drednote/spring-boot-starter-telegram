package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AdvancedScenarioState<E extends Enum<E>> {
    @Getter

    private final List<TelegramRequest> conditiones = new ArrayList<>();
    private E defaultTransitionState;
    @Getter
    @Setter
    private E elseErrorState;
    @Setter
    private E exceptionTransitionState;
    private boolean isFinal;
    @Setter
    private Action executeAction;

    private String nextScenario;
    private E currentStateName;

    public AdvancedScenarioState(E currentStateName) {
        this.currentStateName = currentStateName;
    }

    public List<TelegramRequest> getConditions() {
        return conditiones;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public NextState<E> execute(UserScenarioContext context) {
        try {
            if (executeAction != null) {
                context.getUpdateRequest().getAbsSender().execute(executeAction.execute(context));
                return new NextState<>(defaultTransitionState, nextScenario, true);
            }else{
                return new NextState<>(defaultTransitionState, nextScenario, false);
            }

        } catch (Exception e) {
            if (elseErrorState != null) {
                return new NextState<>(elseErrorState, nextScenario, true);
            } else if (exceptionTransitionState != null) {
                return new NextState<>(exceptionTransitionState, nextScenario, true);
            } else {
                throw new RuntimeException("Unhandled exception in state " + currentStateName, e);
            }
        }
    }

    public static class AdvancedScenarioStateBuilder<E extends Enum<E>> {
        private final E statusName;
        private final Map<E, AdvancedScenarioState<E>> states;
        private final AdvancedScenarioState<E> state;
        private List<TelegramRequest> conditions = new ArrayList<>();
        private E transitionState;
        private final AdvancedScenario<E> advancedScenarioClass;

        public AdvancedScenarioStateBuilder(E statusName, AdvancedScenario<E> advancedScenarioClass) {
            this.statusName = statusName;
            this.states = advancedScenarioClass.getStates();
            this.advancedScenarioClass = advancedScenarioClass;
            this.state = new AdvancedScenarioState<>(statusName);
            states.put(statusName, state);
        }

        public AdvancedScenarioStateBuilder<E> on(TelegramRequest condition) {
            this.conditions.add(condition);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> transitionTo(E nextState) {
            if (this.conditions.isEmpty()) {
                throw new IllegalStateException("Condition must be specified before transition.");
            }
            state.currentStateName = nextState;
            state.conditiones.addAll(conditions);
            this.transitionState = nextState;
            conditions = new ArrayList<>();
            return this;
        }


        public AdvancedScenarioStateBuilder<E> elseErrorTo(E errorState) {
            if (transitionState == null) {
                throw new IllegalStateException("Transition state must be specified before elseErrorTo.");
            }
            state.setElseErrorState(errorState);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> execute(Action action) {
            state.setExecuteAction(action);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> asFinal() {
            state.setFinal(true);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> transitionToScenario(String scenarioName) {
            state.nextScenario = scenarioName;
            state.conditiones.addAll(conditions);
            conditions = new ArrayList<>();
            return this;
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder<E> state(E statusName) {
            state.defaultTransitionState = statusName;
            return new AdvancedScenarioState.AdvancedScenarioStateBuilder<>(statusName, advancedScenarioClass);
        }


        public AdvancedScenario<E> build() {
            return advancedScenarioClass;
        }
    }

    @FunctionalInterface
    public interface Action {
        BotApiMethod<?> execute(UserScenarioContext context);
    }

    @NotNull
    public static TelegramRequestImpl getTelegramRequest(
            String pattern, RequestType requestType, MessageType messageType
    ) {
        return new TelegramRequestImpl(pattern != null ? Set.of(pattern) : Set.of(), requestType != null ? Set.of(requestType) : Set.of(),
                messageType != null ? Set.of(messageType) : Set.of(), true);
    }
}
