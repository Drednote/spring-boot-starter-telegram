package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.request.*;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {

    private final IAdvancedScenarioStorage storage;
    private final HandlerMethodInvoker handlerMethodInvoker;

    public AdvancedScenarioUpdateHandler(IAdvancedScenarioStorage storage, HandlerMethodInvoker handlerMethodInvoker) {
        super();
        this.storage = storage;
        this.handlerMethodInvoker = handlerMethodInvoker;
    }

    @Override
    public void onUpdate(UpdateRequest request) {
        if (request.getAdvancedScenarioManager() != null && !request.getAdvancedScenarioManager().getScenarios().isEmpty()) {
            Optional<IAdvancedScenarioEntity> advancedScenarioEntity = this.storage.findById(request.getUserId() + ":" + request.getChatId());
            UserScenarioContext context = new UserScenarioContext(request, advancedScenarioEntity.map(IAdvancedScenarioEntity::getData).orElse(null));

            String scenarioName = advancedScenarioEntity.map(IAdvancedScenarioEntity::getScenarioName).orElse(null);

            if (scenarioName != null) {
                request.getAdvancedScenarioManager().setCurrentScenario(scenarioName).process(context);
            }
            @NotNull List<UpdateRequestMapping> handlerMethods = request.getAdvancedScenarioManager().getActiveHandlers().stream().map(AdvancedScenarioUpdateHandler::fromTelegramRequest).toList();
            for (UpdateRequestMapping handlerMethod : handlerMethods) {
                if (handlerMethod.matches(request)) {
                    System.out.println("handlerMethod: " + handlerMethod);
                }
            }
          /*  for(UpdateRequestMapping handlerMethod : handlerMethods){
                Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
                ResponseSetter.setResponse(request, invoked, parameterType);
            }*/

        }
    }

    private static UpdateRequestMapping fromTelegramRequest(@NonNull TelegramRequest request) {
        String pattern = request.getPatterns().stream().findFirst().orElse(null);
        RequestType requestType = request.getRequestTypes().stream().findFirst().orElse(null);
        MessageType messageType = request.getMessageTypes().stream().findFirst().orElse(null);

        // Create a Set with a single element if messageType is not null, otherwise an empty Set
        Set<MessageType> messageTypes = messageType != null ? Set.of(messageType) : Collections.emptySet();

        return new UpdateRequestMapping(pattern, requestType, messageTypes, request.exclusiveMessageType());
    }


}
