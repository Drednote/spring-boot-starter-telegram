package io.github.drednote.telegram.handler.scenario.factory;

import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Default implementation of {@code ScenarioIdResolver} which resolves scenario IDs using a combination of in-memory
 * storage and an optional persistence layer.
 *
 * @author Ivan Galushko
 */
public class DefaultScenarioIdResolver implements ScenarioIdResolver {

    private final ScenarioIdRepositoryAdapter adapterProvider;

    public DefaultScenarioIdResolver(ScenarioIdRepositoryAdapter adapterProvider) {
        this.adapterProvider = adapterProvider;
    }

    /**
     * Resolves the scenario ID based on the given update request.
     * <p>
     * If {@code RequestType} is a {@code CALLBACK_QUERY} then id resolves as a previous sent message.
     *
     * @param request the update request from which to resolve the ID
     * @return the resolved scenario ID as a String
     */
    @Override
    public ScenarioIdData resolveId(UpdateRequest request) {
        Set<String> ids = new HashSet<>();
        if (request.getRequestType() == RequestType.CALLBACK_QUERY) {
            Optional.ofNullable(request.getOrigin().getCallbackQuery())
                .map(CallbackQuery::getMessage)
                .map(message -> ScenarioIdResolver.resolveId(request, message))
                .ifPresent(ids::add);
        }
        String fallbackId = doResolve(request);
        return new ScenarioIdData(ids, fallbackId);
    }

    /**
     * Helper method to perform the actual resolution of the scenario ID.
     *
     * @param request the update request used to resolve the ID
     * @return the resolved scenario ID as a String
     */
    @Nullable
    protected String doResolve(UpdateRequest request) {
        String chatId = request.getUserAssociatedId();
        return adapterProvider.findById(chatId)
            .map(ScenarioId::getScenarioId)
            .orElse(null);
    }

    /**
     * Generates a new scenario ID based on the provided update request.
     *
     * @param request the update request used to generate the ID
     * @return the generated scenario ID as a String
     */
    @Override
    public String generateId(UpdateRequest request) {
        return UUID.randomUUID().toString();
    }

    /**
     * Saves a new scenario ID associated with the given update request.
     *
     * @param request the update request with which to associate the new ID
     * @param id      the new scenario ID to be saved
     */
    @Override
    public void saveNewId(UpdateRequest request, String id) {
        String chatId = request.getUserAssociatedId();
        adapterProvider.save(id, chatId);
    }
}
