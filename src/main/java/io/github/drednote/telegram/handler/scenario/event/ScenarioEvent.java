package io.github.drednote.telegram.handler.scenario.event;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

/**
 * Represents a scenario event used for correct matching and processing within scenarios.
 * <p>
 * This class is designed to differentiate between configuration-time and runtime scenario events. When configuring
 * scenarios, events are stored via constructors {@link #ScenarioEvent(TelegramRequest)} and
 * {@link #ScenarioEvent(Set)}, which initialize the {@code mappings} set for flexible future matching. When an actual
 * transition event occurs, {@link #ScenarioEvent(UpdateRequest)} constructor is invoked, assigning an
 * {@link UpdateRequest} to the event.
 * <p>
 * During scenario processing, the stored events enable matching logic based on either the set of mappings or the actual
 * {@code UpdateRequest}. The equality method {@link #equals(Object)} utilizes these for matching logic, checking if the
 * event instances correspond based on their internal data.
 * <p>
 * This design ensures the correct handling of scenario events, supporting configuration, matching, and transition
 * processing with appropriate data encapsulation.
 * <p>
 * The class is tailored to facilitate scenario correctness, encapsulating both the setup phase (using mappings) and the
 * execution phase (using actual events), with matching logic that determines if two events correspond.
 * </p>
 *
 * @author Ivan Galushko
 */
public final class ScenarioEvent {

    /**
     * Optional set of mappings used for matching scenario events during configuration.
     */
    @Nullable
    private final Set<UpdateRequestMapping> mappings;

    /**
     * The actual update request associated with this event during {@link Scenario#sendEvent(UpdateRequest)}.
     */
    @Nullable
    private final UpdateRequest request;

    /**
     * Constructor used during scenario configuration to store mappings for flexible matching.
     *
     * @param request the Telegram request to initialize mappings from; must not be null
     * @throws IllegalArgumentException if request is null
     */
    public ScenarioEvent(TelegramRequest request) {
        Assert.required(request, "TelegramRequest");

        this.mappings = new UpdateRequestMappingBuilder(request).build();
        this.request = null;
    }

    /**
     * Constructor used during scenario configuration to initialize from a set of mappings.
     *
     * @param mapping set of mappings used for matching; must not be null
     * @throws IllegalArgumentException if mapping is null
     */
    public ScenarioEvent(Set<UpdateRequestMapping> mapping) {
        Assert.required(mapping, "Set<UpdateRequestMapping>");

        this.mappings = mapping;
        this.request = null;
    }

    /**
     * Constructor used during scenario {@link Scenario#sendEvent(UpdateRequest)} to create event based on an actual
     * update request.
     *
     * @param request the update request representing an event; must not be null
     * @throws IllegalArgumentException if request is null
     */
    public ScenarioEvent(UpdateRequest request) {
        Assert.required(request, "UpdateRequest");

        this.request = request;
        this.mappings = null;
    }

    /**
     * Retrieves the update request associated with this event.
     *
     * @return the update request
     * @throws IllegalStateException if called when the request is null (e.g., during configuration phase)
     */
    public UpdateRequest getUpdateRequest() {
        if (request == null) {
            throw new IllegalStateException("UpdateRequest is null");
        }
        return request;
    }

    /**
     * Retrieves the set of mappings used for matching during configuration.
     *
     * @return set of mappings, or empty set if none exists
     */
    public Set<UpdateRequestMappingAccessor> getMappings() {
        return mappings == null ? new HashSet<>(0) : mappings.stream()
            .map(m -> ((UpdateRequestMappingAccessor) m))
            .collect(Collectors.toSet());
    }

    /**
     * Compares this {@code ScenarioEvent} to another object for equality.
     * <p>
     * The comparison logic considers whether the events are matching based on their internal data:
     * <ul>
     *   <li>If both have mappings, they are compared for object equality.</li>
     *   <li>If both have update requests, they are compared for object equality.</li>
     *   <li>If one has mappings and the other has a request, attempts to match via {@link UpdateRequestMapping#matches(UpdateRequest)}.</li>
     * </ul>
     *
     * @param o the object to compare to
     * @return true if the events match based on internal logic; false otherwise
     */
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
