package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.utils.Assert;
import java.util.Objects;

public abstract class AbstractState<S> implements State<S> {

    protected final S id;

    protected AbstractState(S id) {
        Assert.required(id, "id");
        this.id = id;
    }

    @Override
    public S getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !AbstractState.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        AbstractState<?> that = (AbstractState<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
