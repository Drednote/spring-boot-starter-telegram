package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.data.State;
import java.util.Map;
import java.util.Set;

/**
 * Interface representing the context of a state.
 *
 * @see State
 * @param <S> the type of the state identifier
 * @author Ivan Galushko
 */
public interface StateContext<S> {

    S id();

    boolean responseMessageProcessing();

    Set<? extends UpdateRequestMappingAccessor> updateRequestMappings();

    Map<String, Object> props();
}
