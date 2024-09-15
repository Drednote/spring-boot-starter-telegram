package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

public abstract class SimpleScenarioBaseTransitionConfigurer<T extends ScenarioBaseTransitionConfigurer<T, S>, S>
    implements ScenarioBaseTransitionConfigurer<T, S> {

    protected final List<Action> actions = new ArrayList<>();
    protected final ScenarioBuilder<S> builder;
    @Nullable
    protected S source;
    @Nullable
    protected S target;
    @Nullable
    protected TelegramRequest request;
    protected boolean callBackQuery;

    protected SimpleScenarioBaseTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public T source(S source) {
        Assert.notNull(source, "Source");
        this.source = source;
        return (T) this;
    }

    @Override
    public T target(S target) {
        Assert.notNull(target, "Target");
        this.target = target;
        return (T) this;
    }

    @Override
    public T action(Action action) {
        Assert.notNull(action, "Action");
        this.actions.add(action);
        return (T) this;
    }

    @Override
    public T telegramRequest(TelegramRequest telegramRequest) {
        Assert.notNull(telegramRequest, "TelegramRequest");
        this.request = telegramRequest;
        return (T) this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() {
        Assert.required(source, "Source");
        Assert.required(target, "Target");
        Assert.required(request, "TelegramRequest");
        builder.addTransition(new TransitionData<>(source, target, actions, request, callBackQuery));
        return new SimpleScenarioTransitionConfigurer<>(builder);
    }
}
