package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEventResult;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

/**
 * Handles update requests by processing associated scenarios in the context of a Telegram bot.
 * <p>
 * The core logic involves retrieving the scenario from the update request, sending the event to it, and then acting
 * based on the result:
 * <ul>
 *   <li>If the event was successful, sets the response if not already set, and updates the scenario's ID.</li>
 *   <li>If the event was not successful and an exception is present, propagates the exception.</li>
 * </ul>
 * <p>
 * This handler enables scenario-based processing for update requests, ensuring state transitions
 * and scenario management are seamlessly integrated into the update flow.
 *
 * @author Ivan Galushko
 */
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class ScenarioUpdateHandler implements UpdateHandler {

    /**
     * Processes an update request by delegating to the associated scenario.
     *
     * @param request the update request containing the scenario and event data
     * @throws Exception if the scenario's event processing results in an exception
     */
    @Override
    public void onUpdate(UpdateRequest request) throws Exception {
        Scenario<?> scenario = request.getScenario();
        if (scenario != null) {
            ScenarioEventResult<?, ?> eventResult = scenario.sendEvent(request);
            if (eventResult.success()) {
                if (request.getResponse() == null) {
                    ResponseSetter.setResponse(request, null);
                }
                String id = scenario.getId();
                ScenarioIdResolver idResolver = scenario.getAccessor().getIdResolver();
                idResolver.saveNewId(request, id);
            } else {
                Exception exception = eventResult.exception();
                if (exception != null) {
                    throw exception;
                }
            }
        }
    }
}
