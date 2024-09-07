package io.github.drednote.telegram.handler.scenario.persist;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleScenarioContext<S> implements ScenarioContext<S> {

    private final String id;
    private final StateContext<S> state;
    private final List<? extends TransitionContext<S>> transitions;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public StateContext<S> getState() {
        return state;
    }

    @Override
    public List<? extends TransitionContext<S>> getTransitionsHistory() {
        return transitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleScenarioContext<?> that = (SimpleScenarioContext<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(state, that.state)
               && Objects.equals(transitions, that.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, transitions);
    }
}
