package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * {@code SimpleActionContext} provides a concrete implementation of the {@code ActionContext}
 * interface.
 *
 * @param <S> the type of the state in the context
 * @author Ivan Galushko
 */
@RequiredArgsConstructor
public class SimpleActionContext<S> implements ActionContext<S> {

    private final UpdateRequest updateRequest;
    private final Transition<S> transition;
    private final Map<String, Object> props;

    /**
     * Retrieves the template variables extracted from the {@code UpdateRequest} based on the
     * mappings defined in the {@code Transition}.
     *
     * @return a map containing the extracted template variables as key-value pairs
     */
    @Override
    public Map<String, String> getTemplateVariables() {
        String text = updateRequest.getText() == null ? "" : updateRequest.getText();
        HashMap<String, String> res = new HashMap<>();
        for (UpdateRequestMappingAccessor mapping : transition.getTarget()
            .getMappings()) {
            String pattern = mapping.getPattern();
            Map<String, String> variables = mapping.getPathMatcher()
                .extractUriTemplateVariables(pattern, text);
            res.putAll(variables);
        }
        return res;
    }

    @Override
    public Map<String, Object> getProps() {
        return props;
    }

    /**
     * Retrieves the transition associated with this context.
     *
     * @return the {@code Transition} object representing the current transition
     */
    @Override
    public Transition<S> getTransition() {
        return transition;
    }

    /**
     * Retrieves the update request associated with this context.
     *
     * @return the {@code UpdateRequest} object representing the current update request
     */
    @Override
    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }
}
