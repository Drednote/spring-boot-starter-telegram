package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(UpdateRequest request) throws Exception;
}
