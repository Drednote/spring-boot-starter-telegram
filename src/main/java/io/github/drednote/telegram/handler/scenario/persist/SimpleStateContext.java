package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import java.util.Set;

public record SimpleStateContext<S>(
    S id,
    Set<? extends UpdateRequestMappingAccessor> updateRequestMappings,
    boolean callbackQuery
) implements StateContext<S> {}
