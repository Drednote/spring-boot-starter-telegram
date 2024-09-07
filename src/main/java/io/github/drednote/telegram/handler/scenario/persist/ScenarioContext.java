package io.github.drednote.telegram.handler.scenario.persist;

import java.util.List;

public interface ScenarioContext<S> {

    String getId();

    StateContext<S> getState();

    List<? extends TransitionContext<S>> getTransitionsHistory();
}
