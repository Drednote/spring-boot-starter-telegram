package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@Slf4j
public class SimpleState<S> extends AbstractState<S> {

    @Nullable
    private Set<UpdateRequestMapping> mappings;

    @Nullable
    private List<Action> actions;

    private boolean callbackQuery;

    public SimpleState(S id) {
        super(id);
    }

    public SimpleState(S id, Set<UpdateRequestMapping> mappings, boolean callbackQuery) {
        super(id);
        this.mappings = mappings;
        this.callbackQuery = callbackQuery;
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

    @Override
    public boolean isCallbackQueryState() {
        return callbackQuery;
    }

    public void setCallbackQuery(boolean callbackQuery) {
        this.callbackQuery = callbackQuery;
    }

    public void setMappings(Set<UpdateRequestMapping> mappings) {
        this.mappings = new HashSet<>(mappings);
    }

    public void setActions(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SimpleState<?> that = (SimpleState<?>) o;
        return Objects.equals(mappings, that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mappings);
    }

    @Override
    public String toString() {
        return "State '%s - %s'".formatted(id, mappings);
    }

    @Override
    public Set<? extends UpdateRequestMappingAccessor> getUpdateRequestMappings() {
        if (mappings == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(mappings);
    }
}
