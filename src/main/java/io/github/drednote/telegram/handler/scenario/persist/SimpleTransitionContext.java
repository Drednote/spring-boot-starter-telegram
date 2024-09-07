package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleTransitionContext<S> implements TransitionContext<S> {

    private final StateContext<S> source;
    private final StateContext<S> target;

    public SimpleTransitionContext(Transition<S> transition) {
        this.source = new SimpleStateContext<>(transition.getSource());
        this.target = new SimpleStateContext<>(transition.getTarget());
    }

    @Override
    public StateContext<S> getSourceContext() {
        return source;
    }

    @Override
    public StateContext<S> getTargetContext() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleTransitionContext<?> that = (SimpleTransitionContext<?>) o;
        return Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
