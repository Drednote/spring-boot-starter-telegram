package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.data.State;

/**
 * Represents a scenario that can manage its state and handle events.
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
     * Retrieves the current state of the scenario.
     *
     * @return the current state as a {@code State<S>} object
     */
    State<S> getState();

    /**
     * Sends an event to the scenario.
     *
     * @param request the update request to be processed
     * @return true if the event was successfully handled, false otherwise
     */
    boolean sendEvent(UpdateRequest request);

    /**
     * Checks if the scenario matches the specified update request.
     *
     * @param request the update request to be matched
     * @return true if the scenario matches the request, false otherwise
     */
    boolean matches(UpdateRequest request);

    /**
     * Checks if the scenario has been terminated.
     * <p>
     * A scenario is usually terminated if no target state where the scenario can go.
     *
     * @return true if the scenario is terminated, false otherwise
     */
    boolean isTerminated();

    /**
     * Retrieves the accessor for the scenario.
     * <p>
     * Usually you don't have to use this method. It is for internal purpose.
     *
     * @return an instance of {@code ScenarioAccessor<S>} that provides access to scenario details and
     * setting opportunity
     */
    ScenarioAccessor<S> getAccessor();
}

