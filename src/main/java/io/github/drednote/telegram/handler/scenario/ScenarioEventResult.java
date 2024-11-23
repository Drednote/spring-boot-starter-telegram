package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.springframework.lang.Nullable;

/**
 * Result of a handling event.
 *
 * @author Ivan Galushko
 * @see Scenario#sendEvent(UpdateRequest)
 */
public interface ScenarioEventResult {

    /**
     * @return true if the event was successfully handled, false otherwise.
     */
    boolean success();

    /**
     * @return exception that thrown during scenario processing, null otherwise.
     */
    @Nullable
    Exception exception();

    /**
     * Default realization of {@code ScenarioEventResult}
     */
    record SimpleScenarioEventResult(
        boolean success, @Nullable Exception exception
    ) implements ScenarioEventResult {}
}
