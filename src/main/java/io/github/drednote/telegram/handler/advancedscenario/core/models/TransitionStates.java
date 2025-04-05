package io.github.drednote.telegram.handler.advancedscenario.core.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TransitionStates<E extends Enum<E>> {
    @NonNull
    private final E defaultTransitionState;
    @Setter
    private E elseErrorState;
    @Setter
    private String toAnotherScenario;

    @NonNull
    private final List<ConditionalTransition<E>> conditionalTransition = new ArrayList<>();

    /**
     * Find actual transition state
     *
     * @param jsonObject
     * @return
     */
    public E getNextTransitionState(JSONObject jsonObject) {
        for (ConditionalTransition<E> transition : conditionalTransition) {
            if (transition.getCondition().test(jsonObject)) {
                return transition.getTransitionState();
            }
        }
        return defaultTransitionState;
    }

    public void addConditionalTransition(ConditionalTransition<E> conditionalTransition) {
        this.conditionalTransition.add(conditionalTransition);
    }

    public TransitionStates(E defaultTransitionState) {
        this.defaultTransitionState = defaultTransitionState;
    }
}
