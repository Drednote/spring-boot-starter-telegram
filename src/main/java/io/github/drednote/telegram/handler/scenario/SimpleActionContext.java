package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.statemachine.StateContext;

/**
 * {@code SimpleActionContext} provides a concrete implementation of the {@code ActionContext}
 * interface.
 *
 * @param <S> the type of the state in the context
 * @author Ivan Galushko
 */
public class SimpleActionContext<S> implements ActionContext<S> {

    private final UpdateRequest updateRequest;
    private final StateContext<S, ScenarioEvent> machineStateContext;
    private final Map<String, Object> props;

    public SimpleActionContext(
        StateContext<S, ScenarioEvent> machineStateContext, Map<String, Object> props
    ) {
        this.updateRequest = machineStateContext.getEvent().getUpdateRequest();
        this.machineStateContext = machineStateContext;
        this.props = props;
    }

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
        Set<UpdateRequestMappingAccessor> mappings = machineStateContext.getTransition().getTrigger().getEvent().getMappings();
        for (UpdateRequestMappingAccessor mapping : mappings) {
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
     * Retrieves the update request associated with this context.
     *
     * @return the {@code UpdateRequest} object representing the current update request
     */
    @Override
    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }

    @Override
    public StateContext<S, ScenarioEvent> getMachineContext() {
        return machineStateContext;
    }
}
