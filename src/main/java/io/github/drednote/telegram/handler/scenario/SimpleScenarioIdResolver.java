package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Default implementation of {@code ScenarioIdResolver} which resolves scenario IDs using a
 * combination of in-memory storage and an optional persistence layer.
 *
 * @author Ivan Galushko
 */
public class SimpleScenarioIdResolver implements ScenarioIdResolver {

    private final FieldProvider<ScenarioIdRepositoryAdapter> adapterProvider;
    private final Map<String, String> inMemoryMap = new HashMap<>();

    public SimpleScenarioIdResolver(FieldProvider<ScenarioIdRepositoryAdapter> adapterProvider) {
        this.adapterProvider = adapterProvider;
    }

    /**
     * Resolves the scenario ID based on the given update request.
     * <p>
     * If {@code RequestType} is a {@code CALLBACK_QUERY} then id resolves as a previous sent
     * message.
     *
     * @param request the update request from which to resolve the ID
     * @return the resolved scenario ID as a String
     */
    @Override
    public String resolveId(UpdateRequest request) {
        if (request.getRequestType() == RequestType.CALLBACK_QUERY) {
            return Optional.ofNullable(request.getOrigin().getCallbackQuery())
                .map(CallbackQuery::getMessage)
                .map(message -> ScenarioIdResolver.resolveId(request, message))
                .orElse(doResolve(request));
        }
        return doResolve(request);
    }

    /**
     * Helper method to perform the actual resolution of the scenario ID.
     *
     * @param request the update request used to resolve the ID
     * @return the resolved scenario ID as a String
     */
    private String doResolve(UpdateRequest request) {
        String chatId = request.getUserAssociatedId();
        if (adapterProvider.isExists()) {
            return adapterProvider.toOptional()
                .flatMap(adapter -> adapter.findById(chatId))
                .map(ScenarioId::getScenarioId)
                .orElseGet(() -> generateId(request));
        }
        return Optional.ofNullable(inMemoryMap.get(chatId))
            .orElseGet(() -> generateId(request));
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
        adapterProvider.toOptional().ifPresentOrElse(
            adapter -> adapter.save(id, chatId),
            () -> inMemoryMap.put(chatId, id)
        );
    }
}
