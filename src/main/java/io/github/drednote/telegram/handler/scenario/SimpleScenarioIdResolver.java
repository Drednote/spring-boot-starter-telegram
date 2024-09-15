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
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;

@RequiredArgsConstructor
public class SimpleScenarioIdResolver implements ScenarioIdResolver {

    private final FieldProvider<ScenarioIdRepositoryAdapter> adapterProvider;
    private final Map<Long, String> inMemoryMap = new HashMap<>();

    @Override
    public String resolveId(UpdateRequest request) {
        if (request.getRequestType() == RequestType.CALLBACK_QUERY) {
            return Optional.ofNullable(request.getOrigin().getCallbackQuery())
                .map(CallbackQuery::getMessage)
                .map(MaybeInaccessibleMessage::getMessageId)
                .map(Object::toString)
                .orElse(doResolve(request));
        }
        return doResolve(request);
    }

    private String doResolve(UpdateRequest request) {
        if (adapterProvider.isExists()) {
            return adapterProvider.toOptional()
                .flatMap(adapter -> adapter.findById(request.getChatId()))
                .map(ScenarioId::getScenarioId)
                .orElseGet(() -> generateId(request));
        }
        return Optional.ofNullable(inMemoryMap.get(request.getChatId()))
            .orElseGet(() -> generateId(request));
    }

    @Override
    public String generateId(UpdateRequest request) {
        return UUID.randomUUID().toString();
    }

    @Override
    public void saveNewId(UpdateRequest request, String id) {
        if (request.getRequestType() == RequestType.CALLBACK_QUERY) {
            return;
        }
        Long chatId = request.getChatId();
        adapterProvider.toOptional().ifPresentOrElse(
            adapter -> adapter.save(id, chatId),
            () -> inMemoryMap.put(chatId, id)
        );
    }
}
