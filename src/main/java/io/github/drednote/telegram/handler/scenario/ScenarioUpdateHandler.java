package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class ScenarioUpdateHandler implements UpdateHandler {

    @Override
    public void onUpdate(UpdateRequest request) throws Exception {
        Scenario<?> scenario = request.getScenario();
        if (scenario != null) {
            ScenarioEventResult eventResult = scenario.sendEvent(request);
            if (eventResult.success()) {
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
