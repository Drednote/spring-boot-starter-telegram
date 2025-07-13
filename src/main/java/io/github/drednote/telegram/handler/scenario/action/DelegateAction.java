package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateContext;

/**
 * A delegate implementation of the {@link org.springframework.statemachine.action.Action} interface, allowing the
 * wrapping of any {@link Action} with customizable properties and error handling.
 * <p>
 * The {@code DelegateAction} executes a wrapped action while managing associated properties and handling errors
 * according to the specified execution context.
 * <p>
 * The property map allows passing additional context or parameters to the delegated action.
 *
 * @param <S> the type of state in the state machine
 * @author Ivan Galushko
 */
public class DelegateAction<S> implements
    org.springframework.statemachine.action.Action<S, ScenarioEvent> {

    private final Action<S> delegate;
    private final Map<String, Object> props;

    /**
     * Constructs a new {@code DelegateAction} with the specified delegate action and optional properties.
     *
     * @param delegate the action to delegate to; must not be null
     * @param props    optional properties to pass to the delegate action; can be null
     * @throws IllegalArgumentException if {@code delegate} is null
     */
    public DelegateAction(Action<S> delegate, @Nullable Map<String, Object> props) {
        Assert.required(delegate, "Delegate Action must not be null");

        this.delegate = delegate;
        this.props = props == null ? new HashMap<>() : props;
    }

    /**
     * Executes the delegate action within an error-handling context.
     * <p>
     * Wraps the execution in a lambda passed to the {@link Actions#withErrorHandling} utility, which ensures exceptions
     * are properly managed and the response is set accordingly.
     * </p>
     *
     * @param context the state context providing the current state and event
     */
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