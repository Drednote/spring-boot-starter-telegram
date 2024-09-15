package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.utils.Assert;
import java.util.Objects;

public class SimpleTransition<S> implements Transition<S> {

    private final State<S> source;
    private final State<S> target;

    public SimpleTransition(State<S> source, State<S> target) {
        Assert.required(source, "source");
        Assert.required(target, "target");

        this.source = source;
        this.target = target;
    }

    @Override
    public State<S> getSource() {
        return source;
    }

    @Override
    public State<S> getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "Transition - {source = %s, target = %s}".formatted(source, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleTransition<?> that = (SimpleTransition<?>) o;
        return Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
