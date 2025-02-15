package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.advancedscenario.core.exceptions.NextTransitionStateException;
import io.github.drednote.telegram.handler.advancedscenario.core.models.TransitionAndNextState;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class AdvancedScenario<E extends Enum<E>> {
    private static final Logger log = LoggerFactory.getLogger(AdvancedScenario.class);

    /**
     * If scenario is sub-scenario is means no access to entry point of scenario
     */
    @Setter
    private boolean isSubScenario;

    /**
     * Basic default state in this scenario
     */
    private final E startState;
    private final Map<E, AdvancedScenarioState<E>> states;
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

    public TransitionContext process(UserScenarioContext context, ScenarioWithState<?> previousState, String currentStateStr) {
        E currentState = currentStateStr == null ? startState : Enum.valueOf(enumClass, currentStateStr);

        AdvancedScenarioState<E> state = states.get(currentState);
        if (state == null) {
            throw new RuntimeException("State not found: " + currentState);
        }

        try {
            if (previousState != null) {
                context.getTransitionContext().setPreviosScenarioWithState(previousState);
                context.getTransitionContext().setNextScenarioWithState(new ScenarioWithState<>(currentState, currentScenarioName));
            }
            // was transferred from another scenario need to execute current execution
            if (context.getTelegramRequest() == null) {
                state.justExecuteAction(context);
                return new TransitionContext(null, new ScenarioWithState<>(Enum.valueOf(enumClass, currentState.toString()), currentScenarioName));
            }

            TransitionAndNextState<E> transitionAndNextState = state.getNextStatus(context);
            ScenarioWithState<E> scenarioWithState;
            if (transitionAndNextState != null && transitionAndNextState.getNextScenarioWithState().getNextScenario() == null) {
                context.getTransitionContext().setPreviosScenarioWithState(new ScenarioWithState<>(currentState, currentScenarioName));
                context.getTransitionContext().setNextScenarioWithState(new ScenarioWithState<>(transitionAndNextState.getNextScenarioWithState().getScenarioState(), currentScenarioName));

                state = states.get(transitionAndNextState.getNextScenarioWithState().getScenarioState());
                scenarioWithState = state.executeActionAndReturnTransition(transitionAndNextState.getTransitionStates(), context);
            } else {
                assert transitionAndNextState != null;
                return new TransitionContext(new ScenarioWithState<>(currentState, currentScenarioName), transitionAndNextState.getNextScenarioWithState());
            }
            if (state.isFinal()) {
                context.setIsFinished(true);
                return new TransitionContext(new ScenarioWithState<>(currentState, currentScenarioName), new ScenarioWithState<>(startState, null));
            } else {
                return new TransitionContext(new ScenarioWithState<>(currentState, currentScenarioName), scenarioWithState);
            }
        } catch (Exception e) {
            Enum<?> errorState = globalErrorTransitionState;
            if (e instanceof NextTransitionStateException) {
                errorState = ((NextTransitionStateException) e).getErrorState();
            }

            if (errorState != null) {
                E errorStateEnum = Enum.valueOf(enumClass, errorState.toString());
                context.getTransitionContext().setNextScenarioWithState(new ScenarioWithState<>(errorStateEnum, currentScenarioName));

                context.setException(e);
                state = states.get(errorStateEnum);
                state.justExecuteAction(context);
                if (state.isFinal()) {
                    context.setIsFinished(true);
                    return new TransitionContext(new ScenarioWithState<>(currentState, currentScenarioName), new ScenarioWithState<>(startState, null));
                } else {
                    return new TransitionContext(new ScenarioWithState<>(currentState, currentScenarioName), new ScenarioWithState<>(errorStateEnum, null));
                }
            } else {
                throw e;
            }
        } finally {
            context.setTransitionContext(new TransitionContext());
        }
    }

    public static class AdvancedScenarioBuilder<E extends Enum<E>> {
        private final AdvancedScenario<E> scenario;

        public AdvancedScenarioBuilder(E startStateName) {
            this.scenario = new AdvancedScenario<>(startStateName, new HashMap<>());
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
