package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {
    @Override
    public void onUpdate(UpdateRequest request) {
        System.out.println("hey");
    }
}
