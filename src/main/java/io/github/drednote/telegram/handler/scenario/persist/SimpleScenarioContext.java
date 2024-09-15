package io.github.drednote.telegram.handler.scenario.persist;

public record SimpleScenarioContext<S>(
    String id, StateContext<S> state
) implements ScenarioContext<S> {}
