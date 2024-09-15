package io.github.drednote.telegram.handler.scenario.persist;

public interface ScenarioContext<S> {

    String id();

    StateContext<S> state();
}
