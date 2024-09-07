package io.github.drednote.telegram.handler.scenario.persist;

public interface ScenarioTransitionContext<S> {

    String getId();

    S getState();

    byte[] getContext();
}
