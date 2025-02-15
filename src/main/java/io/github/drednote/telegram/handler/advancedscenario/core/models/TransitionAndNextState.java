package io.github.drednote.telegram.handler.advancedscenario.core.models;

import io.github.drednote.telegram.handler.advancedscenario.core.ScenarioWithState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransitionAndNextState<E extends Enum<E>> {
    TransitionStates<E> transitionStates;
    ScenarioWithState<E> nextScenarioWithState;
}
