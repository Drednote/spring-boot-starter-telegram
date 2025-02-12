package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {

    private final IAdvancedScenarioStorage storage;

    public AdvancedScenarioUpdateHandler(IAdvancedScenarioStorage storage) {
        super();
        this.storage = storage;
    }

    @Override
    public void onUpdate(UpdateRequest request) {
        if (request.getAdvancedScenarioManager() != null && !request.getAdvancedScenarioManager().getScenarios().isEmpty()) {
            Optional<IAdvancedScenarioEntity> advancedScenarioEntity = this.storage.findById(request.getUserId() + ":" + request.getChatId());
            UserScenarioContext context = new UserScenarioContext(request, advancedScenarioEntity.map(IAdvancedScenarioEntity::getData).orElse(null));

            String scenarioName = advancedScenarioEntity.map(IAdvancedScenarioEntity::getScenarioName).orElse(null);

            if(scenarioName != null){
                request.getAdvancedScenarioManager().setCurrentScenario(scenarioName).process(context);
            }
            //request.getAdvancedScenarioManager().getActiveHandlers().stream().map()
            // UpdateRequestMapping mapping = new UpdateRequestMapping()
            //Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
            //ResponseSetter.setResponse(request, invoked, parameterType);
        }
    }


}
