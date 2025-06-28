package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.handler.scenario.SimpleActionContext;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateContext;

public class DelegateGuard<S> implements org.springframework.statemachine.guard.Guard<S, ScenarioEvent> {

    private final Guard<S> delegate;
    private final Map<String, Object> props;

    public DelegateGuard(Guard<S> delegate, @Nullable Map<String, Object> props) {
        Assert.required(delegate, "Delegate Guard must not be null");

        this.delegate = delegate;
        this.props = props == null ? new HashMap<>() : props;
    }

    @Override
    public boolean evaluate(StateContext<S, ScenarioEvent> context) {
        return delegate.evaluate(new SimpleActionContext<>(context, props));
    }
}
