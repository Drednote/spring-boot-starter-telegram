package io.github.drednote.telegram.handler.advancedscenario.core.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TransitionStates<E extends Enum<E>> {
    private E defaultTransitionState;
    @Setter
    private E elseErrorState;
    @Setter
    private String toAnotherScenario;

    public TransitionStates(E defaultTransitionState) {
        this.defaultTransitionState = defaultTransitionState;
    }
}
