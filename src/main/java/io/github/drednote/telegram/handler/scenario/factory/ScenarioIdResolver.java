package io.github.drednote.telegram.handler.scenario.factory;

import io.github.drednote.telegram.core.request.UpdateRequest;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

/**
 * Interface for resolving and managing scenario IDs based on #{@link UpdateRequest}.
 *
 * @author Ivan Galushko
 */
public interface ScenarioIdResolver {

    /**
     * Resolves the scenario ID based on the provided update request.
     *
     * @param request the update request from which to resolve the ID
     * @return the resolved scenario ID as a String
     */
    ScenarioIdData resolveId(UpdateRequest request);

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

    /**
     * Resolve unique id for message.
     *
     * @param request request
     * @param message message
     * @return unique id
     */
    static String resolveId(UpdateRequest request, MaybeInaccessibleMessage message) {
        return request.getUserAssociatedId() + "_" + message.getMessageId().toString();
    }

    /**
     * @param ids        the list of possible ids. Can be empty.
     * @param fallbackId the default id that stores in the database or in memory. Never null.
     */
    record ScenarioIdData(Set<String> ids, @Nullable String fallbackId) {}
}

