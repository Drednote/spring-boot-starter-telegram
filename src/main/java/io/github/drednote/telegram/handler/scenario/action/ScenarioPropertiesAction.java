package io.github.drednote.telegram.handler.scenario.action;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.ScenarioException;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.Map;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ScenarioPropertiesAction<S> implements Action<S, ScenarioEvent> {

    private final Map<String, Object> properties;

    public ScenarioPropertiesAction(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public void execute(StateContext<S, ScenarioEvent> context) {
        ScenarioEvent event = context.getEvent();
        UpdateRequest request = event.getUpdateRequest();
        Actions.withErrorHandling(() -> properties.forEach((key, value) ->
            request.getScenario().getAccessor().addProperty(key, value)
        ), request);
    }
}