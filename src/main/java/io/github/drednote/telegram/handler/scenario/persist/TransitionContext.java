package io.github.drednote.telegram.handler.scenario.persist;

public interface TransitionContext<S> {

    StateContext<S> getSourceContext();

    StateContext<S> getTargetContext();
}
