package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.lang.Nullable;

/**
 * An abstract implementation of the {@link ScenarioState} interface.
 *
 * @param <S> the type of the state identifier
 * @author Ivan Galushko
 */
public abstract class AbstractScenarioState<S> implements ScenarioState<S> {

    protected final S id;
    @Nullable
    protected Set<UpdateRequestMapping> mappings;

    /**
     * Constructs an AbstractState with the specified id and optional mappings.
     *
     * @param id       the unique identifier of the state; must not be null
     * @param mappings the set of {@link UpdateRequestMapping} associated with this state; may be
     *                 null
     * @throws IllegalArgumentException if id is null
     */
    protected AbstractScenarioState(S id, @Nullable Set<UpdateRequestMapping> mappings) {
        Assert.required(id, "id");
        this.mappings = mappings;
        this.id = id;
    }

    @Override
    public S getId() {
        return id;
    }

    @Override
    public Set<UpdateRequestMappingAccessor> getMappings() {
        if (mappings == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(mappings);
    }

    /**
     * Sets the mappings for this state.
     *
     * @param mappings the set of {@link UpdateRequestMapping} to be associated with this state
     */
    public void setMappings(Set<UpdateRequestMapping> mappings) {
        this.mappings = new HashSet<>(mappings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !AbstractScenarioState.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        AbstractScenarioState<?> that = (AbstractScenarioState<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(mappings, that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mappings);
    }

    @Override
    public String toString() {
        return "State '%s - %s'".formatted(id, mappings);
    }
}
