package io.github.drednote.telegram.handler.scenario.property;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.invoke.InvocableHandlerMethod;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Node;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Request;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Rollback;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Scenario;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties.Scenario.TransitionType;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.model.TransitionData;
import org.springframework.web.method.HandlerMethod;

@BetaApi
public class ScenarioPropertiesConfigurer<S> {

    private final ScenarioProperties scenarioProperties;
    private final ScenarioFactoryResolver scenarioFactoryResolver;
    private final ScenarioBuilder<S> scenarioBuilder;
    private final Set<S> states = new HashSet<>();

    private ScenarioTransitionConfigurer<S> transitionConfigurer;

    public ScenarioPropertiesConfigurer(
        ScenarioBuilder<S> scenarioBuilder,
        ScenarioProperties scenarioProperties,
        ScenarioFactoryResolver scenarioFactoryResolver
    ) {
        Assert.required(scenarioProperties, "ScenarioProperties");
        Assert.required(scenarioFactoryResolver, "ScenarioFactoryResolver");
        Assert.required(scenarioBuilder, "ScenarioBuilder");

        this.scenarioBuilder = scenarioBuilder;
        this.scenarioProperties = scenarioProperties;
        this.scenarioFactoryResolver = scenarioFactoryResolver;
        this.transitionConfigurer = new DefaultScenarioTransitionConfigurer<>(scenarioBuilder);
    }

    public Set<S> collectStates() {
        Map<String, Scenario> values = scenarioProperties.getValues();
        if (values != null) {
            doCollectStates(values);
        }
        return states;
    }

    private void doCollectStates(Map<String, Scenario> values) {
        values.forEach((key, scenario) -> {
            if (scenario != null) {
                if (scenario.getTarget() != null) {
                    states.add((S) scenario.getTarget());
                }
                if (scenario.getSource() != null) {
                    states.add((S) scenario.getSource());
                }

                if (scenario.getSteps() != null && !scenario.getSteps().isEmpty()) {
                    doCollectStates(scenario.getSteps());
                }
            }
        });
    }

    public void configure() {
        Map<String, Scenario> values = scenarioProperties.getValues();
        if (values != null) {
            values.forEach((key, scenario) -> {
                Assert.required(scenario, "Scenario");
                if (scenario.getType() == TransitionType.ROLLBACK) {
                    throw new IllegalArgumentException("First transition cannot be of 'Rollback' type");
                }
                TransitionData<S> transitionData = configureTransition(scenarioBuilder, scenario, null);
                scenario.getGraph().forEach(node -> {
                    doConfigure(scenarioBuilder, transitionData, scenario, node);
                });
            });
        }
    }

    private void doConfigure(
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

    @SneakyThrows
    @NonNull
    private TransitionData<S> configureTransition(
        ScenarioBuilder<S> scenarioBuilder, Scenario scenario, @Nullable TransitionData<S> parent
    ) {
        Request request = scenario.getRequest();
        Set<String> actionClassName = scenario.getActionReferences();
        S target = (S) scenario.getTarget();
        S source = parent != null ? (S) scenario.getSource() : scenarioBuilder.getInitialState();

        Assert.required(target, "Target state");
        Assert.required(source, "Source state");
        Assert.required(scenario, "Scenario");
        Assert.required(request, "Request");

        List<Action<S>> action = createAction(actionClassName);
        TelegramRequest telegramRequest = createTelegramRequest(request);

        TransitionData<S> transitionData = new TransitionData<>(
            source, target, action, telegramRequest, scenario.getProps()
        );

        if (scenario.getType() == TransitionType.RESPONSE_MESSAGE_PROCESSING) {
            var external = transitionConfigurer.withExternal();
            external.inlineKeyboardCreation();
            external.source(source).target(target).telegramRequest(telegramRequest).props(scenario.getProps());
            for (Action<S> objectAction : action) {
                external.action(objectAction);
            }
            transitionConfigurer = external.and();
        } else if (scenario.getType() == TransitionType.ROLLBACK) {
            Rollback rollback = firstNonNull(scenario.getRollback(), scenarioProperties.getDefaultRollback());
            if (parent == null || rollback == null) {
                throw new IllegalArgumentException(
                    "Parent transition or rollback section cannot be null if transition type is Rollback");
            }
            var external = transitionConfigurer.withRollback();
            external.source(source).target(target).telegramRequest(telegramRequest).props(scenario.getProps());
            for (Action<S> objectAction : action) {
                external.action(objectAction);
            }

            external.rollbackProps(parent.getProps())
                .rollbackTelegramRequest(createTelegramRequest(rollback.getRequest()));
            List<Action<S>> actions = createAction(rollback.getActionReferences());
            for (Action<S> objectAction : actions) {
                external.rollbackAction(objectAction);
            }

            transitionConfigurer = external.and();
        } else {
            var external = transitionConfigurer.withExternal();
            external.source(source).target(target).telegramRequest(telegramRequest).props(scenario.getProps());
            for (Action<S> objectAction : action) {
                external.action(objectAction);
            }
            transitionConfigurer = external.and();
        }
        return transitionData;
    }

    private List<Action<S>> createAction(@Nullable Set<String> actionReference) {
        List<Action<S>> response = new ArrayList<>();
        if (actionReference == null) {
            return response;
        }
        for (String name : actionReference) {
            HandlerMethod handlerMethod = scenarioFactoryResolver.resolveAction(name);
            if (handlerMethod == null) {
                throw new IllegalArgumentException("Action class name '" + name + "' not found");
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

    @Data
    @RequiredArgsConstructor
    private static class TransitionData<S> {

        private final S source;
        private final S target;
        private final List<Action<S>> actions;
        private final TelegramRequest request;
        private final Map<String, Object> props;
        private boolean responseMessageProcessing = false;
    }
}
