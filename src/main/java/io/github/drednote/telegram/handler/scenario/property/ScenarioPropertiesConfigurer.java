package io.github.drednote.telegram.handler.scenario.property;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import io.github.drednote.telegram.core.invoke.InvocableHandlerMethod;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Node;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Request;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Rollback;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Scenario;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Scenario.TransitionType;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class ScenarioPropertiesConfigurer {

    private final ScenarioProperties scenarioProperties;
    private final ScenarioFactoryResolver scenarioFactoryResolver;

    public ScenarioPropertiesConfigurer(
        ScenarioProperties scenarioProperties,
        ScenarioFactoryResolver scenarioFactoryResolver
    ) {
        Assert.required(scenarioProperties, "ScenarioProperties");
        Assert.required(scenarioFactoryResolver, "ScenarioFactoryResolver");
        this.scenarioProperties = scenarioProperties;
        this.scenarioFactoryResolver = scenarioFactoryResolver;
    }

    public <S> void configure(ScenarioBuilder<S> scenarioBuilder) {
        Map<String, Scenario> values = scenarioProperties.getValues();
        if (values != null) {
            values.forEach((key, scenario) -> {
                Assert.required(scenario, "Scenario");
                if (scenario.getType() == TransitionType.Rollback) {
                    throw new IllegalArgumentException("First transition cannot be of 'Rollback' type");
                }
                TransitionData<S> transitionData = configureTransition(scenarioBuilder, scenario, null);
                scenario.getGraph().forEach(node -> {
                    doConfigure(scenarioBuilder, transitionData, scenario, node);
                });
            });
        }
    }

    private <S> void doConfigure(
        ScenarioBuilder<S> scenarioBuilder, TransitionData<S> parent, Scenario scenario, Node node
    ) {
        Scenario child = scenario.getSteps().get(node.getId());
        if (child == null) {
            throw new IllegalArgumentException("Step '" + node.getId() + "' does not exist");
        }
        TransitionData<S> transitionData = configureTransition(scenarioBuilder, child, parent);
        node.getChildren().forEach(childNode -> {
            doConfigure(scenarioBuilder, transitionData, scenario, childNode);
        });
    }

    @NotNull
    private <S> TransitionData<S> configureTransition(
        ScenarioBuilder<S> scenarioBuilder, Scenario scenario, @Nullable TransitionData<S> parent
    ) {
        Request request = scenario.getRequest();
        Set<String> actionClassName = scenario.getActionReferences();
        String target = scenario.getTarget();
        Object source = parent != null ? scenario.getSource() : scenarioBuilder.getInitial();

        Assert.required(target, "Target state");
        Assert.required(source, "Source state");
        Assert.required(scenario, "Scenario");
        Assert.required(request, "Request");

        List<Action<Object>> action = createAction(actionClassName);
        TelegramRequest telegramRequest = createTelegramRequest(request);

        TransitionData<Object> transitionData = new TransitionData<>(
            source, target, action, telegramRequest, scenario.getProps()
        );

        if (scenario.getType() == TransitionType.ResponseMessageProcessing) {
            transitionData.setResponseMessageProcessing(true);
        } else if (scenario.getType() == TransitionType.Rollback) {
            Rollback rollback = firstNonNull(scenario.getRollback(), scenarioProperties.getDefaultRollback());
            if (parent == null || rollback == null) {
                throw new IllegalArgumentException(
                    "Parent transition or rollback section cannot be null if transition type is Rollback");
            }
            TransitionData<Object> rollbackTransitionData = new TransitionData<>(
                target, source, createAction(rollback.getActionReferences()),
                createTelegramRequest(rollback.getRequest()), parent.getProps());
            scenarioBuilder.addTransition((TransitionData<S>) rollbackTransitionData);
        }

        scenarioBuilder.addTransition((TransitionData<S>) transitionData);
        return (TransitionData<S>) transitionData;
    }

    private List<Action<Object>> createAction(@Nullable Set<String> actionReference) {
        List<Action<Object>> response = new ArrayList<>();
        if (actionReference == null) {
            return response;
        }
        for (String name : actionReference) {
            HandlerMethod handlerMethod = scenarioFactoryResolver.resolveAction(name);
            if (handlerMethod == null) {
                throw new IllegalArgumentException("Action class name" + name + " not found");
            }
            InvocableHandlerMethod invocableHandlerMethod = new InvocableHandlerMethod(handlerMethod,
                "ScenarioFactory");
            response.add(invocableHandlerMethod::invoke);
        }
        return response;
    }

    @NotNull
    private static TelegramRequest createTelegramRequest(Request request) {
        Set<String> pattern = request.getPatterns();
        Set<RequestType> requestType = request.getRequestTypes();

        Assert.required(pattern, "At least one pattern");
        Assert.required(requestType, "At least one request type");

        return new TelegramRequestImpl(
            pattern, requestType, request.getMessageTypes(), request.isExclusiveMessageType()
        );
    }
}
