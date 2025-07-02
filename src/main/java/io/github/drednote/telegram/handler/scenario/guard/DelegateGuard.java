package io.github.drednote.telegram.handler.scenario.guard;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.action.Actions;
import io.github.drednote.telegram.handler.scenario.action.DefaultActionContext;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
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
        UpdateRequest request = context.getEvent().getUpdateRequest();

        return Actions.withErrorHandling(() -> delegate.evaluate(new DefaultActionContext<>(context, props)), request);
    }
}
