package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@Slf4j
public class SimpleState<S> extends AbstractState<S> {

    @Nullable
    private Set<UpdateRequestMapping> mappings;

    @Nullable
    private List<Action> actions;

    public SimpleState(S id) {
        super(id);
    }

    public SimpleState(S id, Set<UpdateRequestMapping> mappings) {
        super(id);
        this.mappings = mappings;
    }

    @Override
    public boolean matches(UpdateRequest request) {
        if (mappings == null) {
            throw new IllegalStateException("This state cannot used as source");
        }
        return mappings.stream().anyMatch(mapping -> mapping.matches(request));
    }

    @Override
    public Object execute(ActionContext context) {
        if (actions == null) {
            return null;
        }
        return actions.stream().map(action -> action.execute(context)).toList();
    }

    @Nullable
    public Set<UpdateRequestMapping> getMappings() {
        return mappings;
    }

    @Nullable
    public List<Action> getActions() {
        return actions;
    }

    public void setMappings(Set<UpdateRequestMapping> mappings) {
        this.mappings = new HashSet<>(mappings);
    }

    public void setActions(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public String toString() {
        return "SimpleState{" +
               "mappings=" + mappings +
               ", name='" + id + '\'' +
               '}';
    }
}
