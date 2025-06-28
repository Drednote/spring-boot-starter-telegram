package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ScenarioPropertiesAction<S> implements Action<S, ScenarioEvent> {

    private final ScenarioProperties properties;

    public ScenarioPropertiesAction(ScenarioProperties properties) {
        this.properties = properties;
    }

    @Override
    public void execute(StateContext<S, ScenarioEvent> context) {
        ScenarioEvent event = context.getEvent();
        UpdateRequest request = event.getUpdateRequest();
        request.getScenario().getAccessor().addProperties(properties);
    }
}