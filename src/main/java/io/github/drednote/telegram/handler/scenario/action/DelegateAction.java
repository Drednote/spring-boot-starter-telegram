package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateContext;

public class DelegateAction<S> implements
    org.springframework.statemachine.action.Action<S, ScenarioEvent> {

    private final Action<S> delegate;
    private final Map<String, Object> props;

    public DelegateAction(Action<S> delegate, @Nullable Map<String, Object> props) {
        Assert.required(delegate, "Delegate Action must not be null");

        this.delegate = delegate;
        this.props = props == null ? new HashMap<>() : props;
    }

    @Override
    public void execute(StateContext<S, ScenarioEvent> context) {
        ScenarioEvent event = context.getEvent();
        UpdateRequest request = event.getUpdateRequest();

        Actions.withErrorHandling(() -> {
            Object executed = delegate.execute(new DefaultActionContext<>(context, props));
            ResponseSetter.setResponse(request, executed);
        }, request);
    }
}