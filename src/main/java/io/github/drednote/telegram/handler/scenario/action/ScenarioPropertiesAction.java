package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * An implementation of the {@link Action} interface that sets multiple properties on a scenario.
 * <p>
 * When executed, this action iterates over a provided map of properties and adds each key-value pair to the scenario's
 * property collection via its accessor. It uses the {@link Actions#withErrorHandling} utility to ensure that any
 * exceptions during the process are properly managed and associated with the current {@link UpdateRequest}.
 *
 * @param <S> the type of state in the state machine
 * @author Ivan Galushko
 */
public class ScenarioPropertiesAction<S> implements Action<S, ScenarioEvent> {

    private final Map<String, Object> properties;

    /**
     * Constructs a new {@code ScenarioPropertiesAction} with the specified properties to set.
     *
     * @param properties a map of property key-value pairs to add to the scenario, can be null
     */
    public ScenarioPropertiesAction(@Nullable Map<String, Object> properties) {
        this.properties = properties == null ? new HashMap<>() : properties;
    }

    /**
     * Executes the action by adding all specified properties to the scenario.
     * <p>
     * Wraps the property-setting logic within error handling to manage exceptions gracefully.
     * </p>
     *
     * @param context the state context during the state machine transition
     */
    @Override
    public void execute(StateContext<S, ScenarioEvent> context) {
        ScenarioEvent event = context.getEvent();
        UpdateRequest request = event.getUpdateRequest();
        Actions.withErrorHandling(() -> properties.forEach((key, value) ->
            request.getScenario().getAccessor().addProperty(key, value)
        ), request);
    }
}