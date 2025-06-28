package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

/**
 * This class needs for correct mathing scenario events.
 */
public final class ScenarioEvent {

    @Nullable
    private final Set<UpdateRequestMapping> mappings;

    @Nullable
    private final UpdateRequest request;

    public ScenarioEvent(TelegramRequest request) {
        this.mappings = new UpdateRequestMappingBuilder(request).build();
        this.request = null;
    }

    public ScenarioEvent(UpdateRequest request) {
        this.request = request;
        this.mappings = null;
    }

    public UpdateRequest getUpdateRequest() {
        if (request == null) {
            throw new IllegalStateException("UpdateRequest is null");
        }
        return request;
    }

    public Set<UpdateRequestMappingAccessor> getMappings() {
        return mappings == null ? new HashSet<>(0) : mappings.stream()
            .map(m -> ((UpdateRequestMappingAccessor) m))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScenarioEvent that = (ScenarioEvent) o;
        ScenarioEvent f;
        ScenarioEvent s;
        if (this.mappings != null && that.request != null) {
            f = this;
            s = that;
        } else if (that.mappings != null && this.request != null) {
            f = that;
            s = this;
        } else {
            return Objects.equals(this.mappings, that.mappings) && Objects.equals(this.request, that.request);
        }
        return f.mappings.stream().filter(Objects::nonNull).anyMatch(m -> m.matches(s.request));
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappings, request);
    }
}
