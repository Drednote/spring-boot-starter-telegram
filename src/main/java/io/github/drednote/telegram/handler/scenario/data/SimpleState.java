package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@Slf4j
public class SimpleState<S> extends AbstractState<S> {

    @Nullable
    private List<Action<S>> actions;
    @Setter
    private Map<String, Object> props;

    @Getter
    @Setter
    private boolean responseMessageProcessing = false;

    public SimpleState(S id) {
        super(id, null);
        this.props = new HashMap<>();
    }

    public SimpleState(S id, Set<UpdateRequestMapping> mappings, Map<String, Object> props) {
        super(id, mappings);
        this.props = props;
    }

    @Override
    public boolean matches(UpdateRequest request) {
        if (mappings == null) {
            throw new IllegalStateException("This state cannot used as source");
        }
        return mappings.stream().anyMatch(mapping -> mapping.matches(request));
    }

    @Override
    public Object execute(ActionContext<S> context) throws Exception {
        if (actions == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        for (Action<S> action : actions) {
            Object execute = action.execute(context);
            list.add(execute);
        }
        return list;
    }

    @Nullable
    public List<Action<S>> getActions() {
        return actions;
    }

    public void setActions(List<Action<S>> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public Map<String, Object> getProps() {
        return props;
    }
}
