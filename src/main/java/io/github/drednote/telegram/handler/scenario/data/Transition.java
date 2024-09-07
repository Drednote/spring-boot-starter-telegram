package io.github.drednote.telegram.handler.scenario.data;

public interface Transition<S> {

    State<S> getSource();

    State<S> getTarget();
}
