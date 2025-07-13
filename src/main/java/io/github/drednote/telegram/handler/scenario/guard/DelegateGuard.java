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

/**
 * Delegate implementation of {@link org.springframework.statemachine.guard.Guard} that wraps a custom {@link Guard}
 * with additional properties and error handling capabilities.
 * <p>
 * When evaluated, it executes the delegate guard within an error-handling utility, passing a constructed
 * {@link DefaultActionContext} that combines the state context and properties.
 * </p>
 *
 * @param <S> the type of state
 * @author Ivan Galushko
 */
public class DelegateGuard<S> implements org.springframework.statemachine.guard.Guard<S, ScenarioEvent> {

    private final Guard<S> delegate;
    private final Map<String, Object> props;

    /**
     * Constructs a new {@code DelegateGuard} with a delegate guard and optional properties.
     *
     * @param delegate the guard to delegate to; must not be null
     * @param props    optional properties; can be null
     * @throws IllegalArgumentException if delegate is null
     */
    public DelegateGuard(Guard<S> delegate, @Nullable Map<String, Object> props) {
        Assert.required(delegate, "Delegate Guard must not be null");

        this.delegate = delegate;
        this.props = props == null ? new HashMap<>() : props;
    }

    /**
     * Evaluates the guard condition within an error-safe context.
     * <p>
     * Executes the delegate's evaluate method through {@link Actions#withErrorHandling} with a constructed
     * {@link DefaultActionContext} containing the state context and properties.
     * </p>
     *
     * @param context the state context during transition
     * @return true if the guard condition is satisfied; false or exception if evaluation fails
     */
    @Override
    public boolean evaluate(StateContext<S, ScenarioEvent> context) {
        UpdateRequest request = context.getEvent().getUpdateRequest();

        return Actions.withErrorHandling(() -> delegate.evaluate(new DefaultActionContext<>(context, props)), request);
    }
}
