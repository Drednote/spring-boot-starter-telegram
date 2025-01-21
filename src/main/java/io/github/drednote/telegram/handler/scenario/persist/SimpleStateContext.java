package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import java.util.Map;
import java.util.Set;

public record SimpleStateContext<S>(
    S id,
    Set<? extends UpdateRequestMappingAccessor> updateRequestMappings,
    boolean responseMessageProcessing,
    Map<String, Object> props
) implements StateContext<S> {}
