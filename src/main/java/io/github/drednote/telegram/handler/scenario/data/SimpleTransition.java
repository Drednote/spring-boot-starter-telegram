package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.utils.Assert;

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
        return "SimpleTransition{" +
               "source=" + source +
               ", target=" + target +
               '}';
    }
}
