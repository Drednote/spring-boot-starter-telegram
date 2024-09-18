package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.Map;

public interface ActionContext<S> {

    UpdateRequest getUpdateRequest();

    Transition<S> getTransition();

    Map<String, String> getTemplateVariables();
}
