package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

public abstract class SimpleScenarioBaseTransitionConfigurer<C extends ScenarioBaseTransitionConfigurer<C, S>, S>
    implements ScenarioBaseTransitionConfigurer<C, S> {

    protected final List<Action<S>> actions = new ArrayList<>();
    protected final ScenarioBuilder<S> builder;
    @Nullable
    protected S source;
    @Nullable
    protected S target;
    @Nullable
    protected TelegramRequest request;
    protected boolean overrideGlobalScenarioId = false;

    protected SimpleScenarioBaseTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public C source(S source) {
        Assert.notNull(source, "Source");
        this.source = source;
        return (C) this;
    }

    @Override
    public C target(S target) {
        Assert.notNull(target, "Target");
        this.target = target;
        return (C) this;
    }

    @Override
    public C action(Action<S> action) {
        Assert.notNull(action, "Action");
        this.actions.add(action);
        return (C) this;
    }

    @Override
    public C telegramRequest(TelegramRequest telegramRequest) {
        Assert.notNull(telegramRequest, "TelegramRequest");
        this.request = telegramRequest;
        return (C) this;
    }

    @Override
    public C overrideGlobalScenarioId() {
        this.overrideGlobalScenarioId = true;
        return (C) this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() {
        Assert.required(source, "Source");
        Assert.required(target, "Target");
        Assert.required(request, "TelegramRequest");
        TransitionData<S> transition = new TransitionData<>(source, target, actions, request, overrideGlobalScenarioId);
        beforeAnd(transition);
        builder.addTransition(transition);
        return new SimpleScenarioTransitionConfigurer<>(builder);
    }

    protected void beforeAnd(TransitionData<S> data) {
        // nothing in default impl
    }
}
