package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;

/**
 * Interface for resolving and managing scenario IDs based on #{@link UpdateRequest}.
 *
 * @author Ivan Galushko
 */
@BetaApi
public interface ScenarioIdResolver {

    /**
     * Resolves the scenario ID based on the provided update request.
     *
     * @param request the update request from which to resolve the ID
     * @return the resolved scenario ID as a String
     */
    String resolveId(UpdateRequest request);

    /**
     * Generates a new scenario ID based on the provided update request.
     *
     * @param request the update request used to generate the ID
     * @return the generated scenario ID as a String
     */
    String generateId(UpdateRequest request);

    /**
     * Saves a new scenario ID associated with the given update request.
     *
     * @param request the update request with which to associate the new ID
     * @param id      the new scenario ID to be saved
     */
    void saveNewId(UpdateRequest request, String id);
}

