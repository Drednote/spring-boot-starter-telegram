package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleActionContext<S> implements ActionContext<S> {

    private final UpdateRequest updateRequest;
    private final Transition<S> transition;

    @Override
    public Map<String, String> getTemplateVariables() {
        String text = updateRequest.getText() == null ? "" : updateRequest.getText();
        HashMap<String, String> res = new HashMap<>();
        for (UpdateRequestMappingAccessor mapping : transition.getTarget().getUpdateRequestMappings()) {
            String pattern = mapping.getPattern();
            Map<String, String> variables = mapping.getPathMatcher().extractUriTemplateVariables(pattern, text);
            res.putAll(variables);
        }
        return res;
    }

    @Override
    public Transition<S> getTransition() {
        return transition;
    }

    @Override
    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }
}
