package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

/**
 * An abstract base class for configuring scenario transitions.
 *
 * @param <C> the type of the concrete configurer
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
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

    /**
     * Constructs a SimpleScenarioBaseTransitionConfigurer with a ScenarioBuilder.
     *
     * @param builder the builder used for configuring scenarios
     */
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
    public ScenarioTransitionConfigurer<S> and() {
        Assert.required(source, "Source");
        Assert.required(target, "Target");
        Assert.required(request, "TelegramRequest");
        TransitionData<S> transition = new TransitionData<>(source, target, actions, request);
        beforeAnd(transition);
        builder.addTransition(transition);
        return new SimpleScenarioTransitionConfigurer<>(builder);
    }

    /**
     * Hook for additional processing before adding a transition.
     *
     * @param data the transition data to process
     */
    protected void beforeAnd(TransitionData<S> data) {
        // nothing in default impl
    }
}
