package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;

@FunctionalInterface
public interface ScenarioIdResolver {

    String resolveId(UpdateRequest request);
}
