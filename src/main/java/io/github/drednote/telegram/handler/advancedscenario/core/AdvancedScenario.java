package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.advancedscenario.core.exceptions.NextTransitionStateException;
import io.github.drednote.telegram.handler.advancedscenario.core.models.TransitionAndNextState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class AdvancedScenario<E extends Enum<E>> {
    private final E startState;
    private Map<E, AdvancedScenarioState<E>> states = new HashMap<>();
    @Setter
    private E globalErrorTransitionState;

    @Setter
    private String currentScenarioName;

    private Class<E> enumClass;

    public static <T extends Enum<T>> AdvancedScenarioBuilder<T> create(T startStateName) {
        return new AdvancedScenarioBuilder<>(startStateName);
    }

    public AdvancedScenario(E startState, Map<E, AdvancedScenarioState<E>> states) {
        this.startState = startState;
        this.states = states;
    }


    public List<TelegramRequest> getActiveConditions(String currentStateStr) {
        E currentState = currentStateStr == null ? startState : Enum.valueOf(enumClass, currentStateStr);
        AdvancedScenarioState<E> currentStateObj = states.get(currentState);
        if (currentStateObj == null) {
            throw new RuntimeException("Current state not found: " + currentState);
        }
        return currentStateObj.getConditions();
    }

    public NextActualState<E> process(UserScenarioContext context, String currentStateStr) {
        E currentState = currentStateStr == null ? startState : Enum.valueOf(enumClass, currentStateStr);
        AdvancedScenarioState<E> state = states.get(currentState);
        if (state == null) {
            throw new RuntimeException("State not found: " + currentState);
        }

        try {

            // was transferred from another scenario need to execute current execution
            if (context.getTelegramRequest() == null) {
                state.justExecuteAction(context);
                return new NextActualState<>(Enum.valueOf(enumClass, currentState.toString()), currentScenarioName);
            }

            TransitionAndNextState<E> transitionAndNextState = state.getNextStatus(context);
            NextActualState<E> nextActualState;
            if (transitionAndNextState != null && transitionAndNextState.getNextActualState().getNextScenario() == null) {
                state = states.get(transitionAndNextState.getNextActualState().getScenarioState());
                nextActualState = state.executeActionAndReturnTransition(transitionAndNextState.getTransitionStates(), context);
            } else {
                assert transitionAndNextState != null;
                return transitionAndNextState.getNextActualState();
            }
            if (state.isFinal()) {
                context.setIsFinished(true);
                return new NextActualState<>(startState, null);
            } else {
                return nextActualState;
            }
        } catch (Exception e) {
            Enum<?> errorState = globalErrorTransitionState;
            if (e instanceof NextTransitionStateException) {
                errorState = ((NextTransitionStateException) e).getErrorState();
            }

            if (errorState != null) {
                context.setException(e);
                state = states.get(errorState);
                state.justExecuteAction(context);
                if (state.isFinal()) {
                    context.setIsFinished(true);
                    return new NextActualState<>(startState, null);
                } else {
                    return new NextActualState<>(Enum.valueOf(enumClass, errorState.toString()), null);
                }
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
