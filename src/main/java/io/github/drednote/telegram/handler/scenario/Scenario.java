package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEventResult;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateMachine;

/**
 * Represents a scenario that can manage its state and handle events within a Telegram bot context.
 * <p>
 * The {@code Scenario} interface is designed to model a stateful interaction scenario, with capabilities such as event
 * processing, property management, state inspection, and identification. It is intended for use in workflows where
 * different scenarios are managed via a state machine and can respond to incoming update requests.
 * <p>
 * Implementations of this interface are expected to encapsulate behavior related to managing scenario states and
 * reacting to events, including switching states and handling properties that influence scenario behavior.
 *
 * @param <S> the type of the state managed by the scenario
 * @author Ivan Galushko
 */
public interface Scenario<S> {

    /**
     * Retrieves the unique identifier of the scenario.
     *
     * @return the unique identifier as a String
     */
    String getId();

    /**
     * Retrieves a property associated with the current scenario by key.
     * <p>
     * During scenario processing, some algorithms can add any property to the scenario instance. It allows flexible
     * extension of scenario metadata without modifying core structure.
     * </p>
     *
     * @param key string key, not null
     * @param <T> type of property
     * @return value of property, nullable; may return null if property is absent
     * @throws ClassCastException if the stored property does not match the requested type
     */
    @Nullable
    <T> T getProperty(String key);

    /**
     * Retrieves the associated {@link StateMachine} instance on which the scenario is based.
     * <p>
     * The state machine governs the scenario's possible states and transitions and provides control over the scenario's
     * progression.
     *
     * @return the instance of the state machine
     */
    StateMachine<S, ScenarioEvent> getStateMachine();

    /**
     * Sends an {@link UpdateRequest} event to the scenario and processes it through the scenario's state machine.
     * <p>
     * The method returns a {@link ScenarioEventResult} indicating whether the event was accepted, along with results of
     * processing and any associated exception.
     *
     * @param request the update request to be processed
     * @return the result of event handling encapsulated in a {@code ScenarioEventResult}
     */
    ScenarioEventResult<S, ScenarioEvent> sendEvent(UpdateRequest request);

    /**
     * Checks if the scenario matches the given {@link UpdateRequest}.
     * <p>
     * Matching is typically based on the properties of the event and the current state, determining whether this
     * scenario should handle the event.
     *
     * @param request the update request to be matched
     * @return true if the scenario matches the request, false otherwise
     */
    boolean matches(UpdateRequest request);

    /**
     * Determines if the scenario has been terminated.
     * <p>
     * A scenario is considered terminated if it has no valid target state to transition to, implying completion of its
     * workflow.
     *
     * @return true if the scenario is terminated, false otherwise
     */
    boolean isTerminated();

    /**
     * Retrieves the {@link ScenarioAccessor} for internal manipulation and access to scenario details.
     * <p>
     * Usually, typical usage doesn't require calling this method. It's mainly for internal operations such as resetting
     * or inspecting scenario states.
     *
     * @return an instance of {@code ScenarioAccessor<S>} that provides access to scenario details and setting
     * opportunities
     */
    ScenarioAccessor<S> getAccessor();
}

