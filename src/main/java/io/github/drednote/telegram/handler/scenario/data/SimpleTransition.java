package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.utils.Assert;
import java.util.Objects;

public class SimpleTransition<S> implements Transition<S> {

    private final ScenarioState<S> source;
    private final ScenarioState<S> target;

    public SimpleTransition(ScenarioState<S> source, ScenarioState<S> target) {
        Assert.required(source, "source");
        Assert.required(target, "target");

        this.source = source;
        this.target = target;
    }

    @Override
    public ScenarioState<S> getSource() {
        return source;
    }

    @Override
    public ScenarioState<S> getTarget() {
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
