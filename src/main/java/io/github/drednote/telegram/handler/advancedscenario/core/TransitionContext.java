package io.github.drednote.telegram.handler.advancedscenario.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TransitionContext {
    ScenarioWithState<?> previosScenarioWithState;
    ScenarioWithState<?> nextScenarioWithState;
}
