package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.handler.advancedscenario.core.exceptions.AdvancedScenarioLogicException;
import io.github.drednote.telegram.handler.advancedscenario.core.exceptions.NextTransitionStateException;
import io.github.drednote.telegram.handler.advancedscenario.core.models.ConditionalTransition;
import io.github.drednote.telegram.handler.advancedscenario.core.models.TransitionAndNextState;
import io.github.drednote.telegram.handler.advancedscenario.core.models.TransitionStates;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AdvancedScenarioState<E extends Enum<E>> {
    private static final Logger log = LoggerFactory.getLogger(AdvancedScenarioState.class);

    private final Map<List<TelegramRequest>, TransitionStates<E>> conditions = new HashMap<>();

    private boolean isFinal;
    @Setter
    private Action executeAction;

    private final E currentStateName;

    public AdvancedScenarioState(E currentStateName) {
        this.currentStateName = currentStateName;
    }

    public List<TelegramRequest> getConditions() {
        return conditions.keySet()
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

    public TransitionAndNextState<E> getNextStatus(UserScenarioContext context) {
        TransitionStates<E> currentTransitionState = findTransitionStatesByRequest(context.getTelegramRequest());
        return new TransitionAndNextState<>(currentTransitionState, new ScenarioWithState<>(currentTransitionState.getNextTransitionState(context.getData()), currentTransitionState.getToAnotherScenario()));
    }

    public void justExecuteAction(UserScenarioContext context) {
        log.info("Previous state: {}, next state: {}", context.getTransitionContext().getPreviosScenarioWithState(), context.getTransitionContext().getNextScenarioWithState());
        if (executeAction != null) {
            ResponseSetter.setResponse(context.getUpdateRequest(), executeAction.execute(context));
        } else {
            throw new RuntimeException("No execution action exist in " + currentStateName);
        }
    }

    public ScenarioWithState<E> executeActionAndReturnTransition(TransitionStates<E> transitionStates, UserScenarioContext context) {
        log.info("Previous state: {}, next state: {}", context.getTransitionContext().getPreviosScenarioWithState(), context.getTransitionContext().getNextScenarioWithState());
        try {
            if (executeAction != null) {
                ResponseSetter.setResponse(context.getUpdateRequest(), executeAction.execute(context));
                return new ScenarioWithState<>(transitionStates.getDefaultTransitionState(), transitionStates.getToAnotherScenario());
            } else {
                throw new RuntimeException("No execution action exist in " + currentStateName);
            }
        } catch (Exception e) {
            if (transitionStates.getElseErrorState() != null) {
                throw new NextTransitionStateException(transitionStates.getElseErrorState(), e.getMessage(), e);
            } else {
                throw new RuntimeException("Unhandled exception in state " + currentStateName, e);
            }
        }
    }

    private TransitionStates<E> findTransitionStatesByRequest(TelegramRequest request) {
        for (Map.Entry<List<TelegramRequest>, TransitionStates<E>> entry : conditions.entrySet()) {
            List<TelegramRequest> keyList = entry.getKey();
            if (keyList.contains(request)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Request " + request + " not found in state " + currentStateName);
    }

    public static class AdvancedScenarioStateBuilder<E extends Enum<E>> {
        private final Map<E, AdvancedScenarioState<E>> states;
        private final AdvancedScenarioState<E> state;
        private List<TelegramRequest> conditions = new ArrayList<>();
        private E transitionState;
        private final AdvancedScenario<E> advancedScenarioClass;
        private TransitionStates<E> currentTransitionState;

        public AdvancedScenarioStateBuilder(E statusName, AdvancedScenario<E> advancedScenarioClass) {
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
            this.transitionState = nextState;
            this.currentTransitionState = new TransitionStates<>(transitionState);
            state.conditions.put(conditions, currentTransitionState);

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

        public AdvancedScenarioStateBuilder<E> conditionalTransition(Predicate<JSONObject> predicate, E nextState) {
            ConditionalTransition<E> conditionalTransition = new ConditionalTransition<>(nextState, predicate);
            this.currentTransitionState.addConditionalTransition(conditionalTransition);
            return this;
        }

        public AdvancedScenarioStateBuilder<E> transitionToScenario(String scenarioName) {
            this.transitionState = null;
            this.currentTransitionState = new TransitionStates<>(transitionState);
            this.currentTransitionState.setToAnotherScenario(scenarioName);
            state.conditions.put(conditions, currentTransitionState);
            return this;
        }

        public AdvancedScenarioState.AdvancedScenarioStateBuilder<E> state(E statusName) {
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
