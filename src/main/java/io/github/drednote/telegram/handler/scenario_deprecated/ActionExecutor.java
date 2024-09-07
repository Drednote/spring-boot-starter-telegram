package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.request.UpdateRequest;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(UpdateRequest request) throws Exception;
}
