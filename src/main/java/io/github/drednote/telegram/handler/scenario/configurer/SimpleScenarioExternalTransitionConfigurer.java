package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

public class SimpleScenarioExternalTransitionConfigurer<S> implements ScenarioExternalTransitionConfigurer<S> {

    private final List<Action> actions = new ArrayList<>();
    private final ScenarioBuilder<S> builder;
    @Nullable
    private S source;
    @Nullable
    private S target;
    @Nullable
    private TelegramRequest request;

    public SimpleScenarioExternalTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> source(S source) {
        Assert.notNull(source, "Source");
        this.source = source;
        return this;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> target(S target) {
        Assert.notNull(target, "Target");
        this.target = target;
        return this;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> action(Action action) {
        Assert.notNull(action, "Action");
        this.actions.add(action);
        return this;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> telegramRequest(TelegramRequest telegramRequest) {
        Assert.notNull(telegramRequest, "TelegramRequest");
        this.request = telegramRequest;
        return this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() {
        Assert.required(source, "Source");
        Assert.required(target, "Target");
        Assert.required(request, "TelegramRequest");
        builder.addTransition(new TransitionData<>(source, target, actions, request));
        return new SimpleScenarioTransitionConfigurer<>(builder);
    }
}
