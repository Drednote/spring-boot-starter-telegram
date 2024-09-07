package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;

public interface ActionContext {

    UpdateRequest getUpdateRequest();

//    UpdateRequestMapping getMapping();
}
