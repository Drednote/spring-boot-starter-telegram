package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.matcher.RequestMatcher;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioResponseMessageTransitionConfigurer;
import java.util.Set;

/**
 * Represents a State in the scenario.
 *
 * @param <S> the type of the state identifier
 * @author Ivan Galushko
 */
public interface State<S> extends RequestMatcher, Action<S> {

    /**
     * Retrieves the unique identifier of the state.
     *
     * @return the unique identifier of the state
     */
    S getId();

    /**
     * Checks whether the response message processing is enabled. Look at
     * {@link ScenarioResponseMessageTransitionConfigurer} for details.
     *
     * @return {@code true} if response message processing is enabled; {@code false} otherwise
     * @see ScenarioResponseMessageTransitionConfigurer
     */
    boolean isResponseMessageProcessing();

    /**
     * Retrieves the set of update request mappings associated with this state.
     *
     * @return a set of {@link UpdateRequestMappingAccessor} representing the update request
     * mappings
     */
    Set<UpdateRequestMappingAccessor> getMappings();
}

