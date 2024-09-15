package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;

public interface ScenarioIdResolver {

    String resolveId(UpdateRequest request);

    String generateId(UpdateRequest request);

    void saveNewId(UpdateRequest request, String id);
}
