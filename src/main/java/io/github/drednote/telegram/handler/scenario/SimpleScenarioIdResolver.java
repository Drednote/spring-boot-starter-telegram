package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;

public class SimpleScenarioIdResolver implements ScenarioIdResolver {

    @Override
    public String resolveId(UpdateRequest request) {
        return request.getChatId().toString();
    }
}
