package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleActionContext implements ActionContext {

    private final UpdateRequest updateRequest;
//    private final UpdateRequestMapping mapping;

    @Override
    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }

//    @Override
//    public UpdateRequestMapping getMapping() {
//        return mapping;
//    }
}
