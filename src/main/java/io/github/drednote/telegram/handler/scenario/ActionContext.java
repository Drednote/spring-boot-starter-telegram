package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import java.util.Map;
import org.springframework.statemachine.StateContext;
import org.springframework.util.PathMatcher;

/**
 * Interface for defining the action context.
 *
 * @param <S> the type of the state associated with the action context
 * @author Ivan Galushko
 */
public interface ActionContext<S> {

    /**
     * Retrieves the update request associated with the action context.
     *
     * @return the UpdateRequest for the action context
     */
    UpdateRequest getUpdateRequest();

    /**
     * Retrieves the context associated with the action context.
     *
     * @return the {@code StateContext} for the action context
     */
    StateContext<S, ScenarioEvent> getMachineContext();

    /**
     * Retrieves the template variables associated with the action context.
     * <p>
     * Variables are extracting by {@link PathMatcher}.
     * <p>
     * Example of configuring transition if you want extract variables:
     * <pre>{@code
     *  telegramRequest(callbackQuery("Variable={value:.*}"))
     * }</pre>
     * If the message is "Variable=foo", then you will get a map {@code value=foo}
     *
     * @return a map of template variable names to their corresponding values
     */
    Map<String, String> getTemplateVariables();

    Map<String, Object> getProps();
}
