package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.handler.advancedscenario.core.models.TransitionStates;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.*;
import java.util.stream.Collectors;


public class AdvancedScenarioState<E extends Enum<E>> {
    @Getter

    private Map<List<TelegramRequest>, TransitionStates<E>> conditiones = new HashMap<>();
    private E defaultTransitionState;

    private boolean isFinal;
    @Setter
    private Action executeAction;

    private E currentStateName;

    public AdvancedScenarioState(E currentStateName) {
        this.currentStateName = currentStateName;
    }

    public List<TelegramRequest> getConditions() {
        return conditiones.keySet()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public NextState<E> execute(UserScenarioContext context) {
        TransitionStates<E> currentTransitionState = findTransitionStatesByRequest(context.getTelegramRequest());
        try {
            if (executeAction != null) {
                context.getUpdateRequest().getAbsSender().execute(executeAction.execute(context));
                return new NextState<>(currentTransitionState.getDefaultTransitionState(), currentTransitionState.getToAnotherScenario(), true);
            } else {
                return new NextState<>(defaultTransitionState, currentTransitionState.getToAnotherScenario(), false);
            }

        } catch (Exception e) {
            if (currentTransitionState.getElseErrorState() != null) {
                return new NextState<>(currentTransitionState.getElseErrorState(), currentTransitionState.getToAnotherScenario(), true);
            } else {
                throw new RuntimeException("Unhandled exception in state " + currentStateName, e);
            }
        }
    }

    private TransitionStates<E> findTransitionStatesByRequest(TelegramRequest request) {
        for (Map.Entry<List<TelegramRequest>, TransitionStates<E>> entry : conditiones.entrySet()) {
            List<TelegramRequest> keyList = entry.getKey();
            if (keyList.contains(request)) { // Проверяем, содержит ли список данный TelegramRequest
                return entry.getValue(); // Возвращаем соответствующее TransitionStates<E>
            }
        }
        return null; // Если ничего не найдено, возвращаем null
    }

    public static class AdvancedScenarioStateBuilder<E extends Enum<E>> {
        private final E statusName;
        private final Map<E, AdvancedScenarioState<E>> states;
        private final AdvancedScenarioState<E> state;
        private List<TelegramRequest> conditions = new ArrayList<>();
        private E transitionState;
        private final AdvancedScenario<E> advancedScenarioClass;
        private TransitionStates<E> currentTransitionState;

        public AdvancedScenarioStateBuilder(E statusName, AdvancedScenario<E> advancedScenarioClass) {
            this.statusName = statusName;
            this.states = advancedScenarioClass.getStates();
            this.advancedScenarioClass = advancedScenarioClass;
            this.state = new AdvancedScenarioState<>(statusName);
            states.put(statusName, state);
        }

        public AdvancedScenarioStateBuilder<E> on(TelegramRequest condition) {
            conditions = new ArrayList<>();
            this.currentTransitionState = null;
            this.conditions.add(condition);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> or(TelegramRequest condition) {
            if (this.conditions.isEmpty()) {
                throw new IllegalStateException("Use on before or.");
            }
            this.conditions.add(condition);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> transitionTo(E nextState) {
            if (this.conditions.isEmpty()) {
                throw new IllegalStateException("Condition must be specified before transition.");
            }
            state.currentStateName = nextState;
            this.transitionState = nextState;
            this.currentTransitionState = new TransitionStates<>(transitionState);
            state.conditiones.put(conditions, currentTransitionState);

            return this;
        }


        public AdvancedScenarioStateBuilder<E> elseErrorTo(E errorState) {
            if (transitionState == null) {
                throw new IllegalStateException("Transition state must be specified before elseErrorTo.");
            }
            this.currentTransitionState.setElseErrorState(errorState);
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
            this.transitionState = null;
            this.currentTransitionState = new TransitionStates<>(transitionState);
            this.currentTransitionState.setToAnotherScenario(scenarioName);
            state.conditiones.put(conditions, currentTransitionState);
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
