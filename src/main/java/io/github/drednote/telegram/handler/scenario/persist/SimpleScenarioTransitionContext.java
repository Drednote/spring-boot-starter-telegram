package io.github.drednote.telegram.handler.scenario.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleScenarioTransitionContext<S> implements ScenarioTransitionContext<S> {

    private String id;
    private S state;
    private byte[] context;
}