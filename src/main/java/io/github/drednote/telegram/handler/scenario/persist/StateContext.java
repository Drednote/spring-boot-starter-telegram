package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import java.util.Set;

public interface StateContext<S> {

    S id();

    boolean callbackQuery();

    Set<? extends UpdateRequestMappingAccessor> updateRequestMappings();
}
