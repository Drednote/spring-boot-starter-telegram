package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.action.DelegateAction;
import io.github.drednote.telegram.handler.scenario.guard.DelegateGuard;
import io.github.drednote.telegram.handler.scenario.guard.Guard;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.TransitionConfigurer;
import org.springframework.statemachine.security.SecurityRule.ComparisonType;

/**
 * An abstract base class for configuring scenario transitions.
 *
 * @param <C> the type of the concrete configurer
 * @param <S> the type of the getMachine
 * @author Ivan Galushko
 */
public abstract class AbstractScenarioBaseTransitionConfigurer<T, C extends ScenarioBaseTransitionConfigurer<C, S>, S>
    implements ScenarioBaseTransitionConfigurer<C, S> {

    protected final ScenarioBuilder<S> builder;

    protected final List<Pair<Action<S>, Action<S>>> actions = new ArrayList<>();
    protected final Map<String, Object> props = new HashMap<>();

    @Nullable
    protected Guard<S> guard;
    @Nullable
    protected S source;
    @Nullable
    protected S target;
    @Nullable
    protected S state;
    @Nullable
    protected TelegramRequest request;
    @Nullable
    protected String name;
    @Nullable
    protected Long timer;
    @Nullable
    protected Long timerOnce;
    @Nullable
    protected String attributes;
    @Nullable
    protected String expression;
    @Nullable
    protected ComparisonType match;

    protected AbstractScenarioBaseTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public C source(S source) {
        this.source = source;
        return (C) this;
    }

    @Override
    public C action(Action<S> action) {
        this.actions.add(Pair.of(action, null));
        return (C) this;
    }

    @Override
    public C telegramRequest(TelegramRequest telegramRequest) {
        this.request = telegramRequest;
        return (C) this;
    }

    @Override
    public C props(Map<String, Object> props) {
        this.props.putAll(props);
        return (C) this;
    }

    @Override
    public C state(S state) {
        this.state = state;
        return (C) this;
    }

    @Override
    public C action(Action<S> action, Action<S> error) {
        this.actions.add(Pair.of(action, error));
        return (C) this;
    }

    @Override
    public C guard(Guard<S> guard) {
        this.guard = guard;
        return (C) this;
    }

    @Override
    public C secured(String attributes, ComparisonType match) {
        this.attributes = attributes;
        this.match = match;
        return (C) this;
    }

    @Override
    public C secured(String expression) {
        this.expression = expression;
        return (C) this;
    }

    @Override
    public C name(String name) {
        this.name = name;
        return (C) this;
    }

    @Override
    public C timer(long period) {
        this.timer = period;
        return (C) this;
    }

    @Override
    public C timerOnce(long period) {
        this.timerOnce = period;
        return (C) this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() throws Exception {
        Assert.required(source, "Source");
        Assert.required(request, "TelegramRequest");

        return new DefaultScenarioTransitionConfigurer<>(builder, build());
    }

    protected void preBuild(TransitionConfigurer<T, S, ScenarioEvent> configurer) {
        if (source != null) {
            configurer.source(source);
        }
        if (state != null) {
            configurer.state(state);
        }

        if (request != null) {
            configurer.event(new ScenarioEvent(request));
        }
        if (name != null) {
            configurer.name(name);
        }

        if (timer != null) {
            configurer.timer(timer);
        }
        if (timerOnce != null) {
            configurer.timerOnce(timerOnce);
        }

        if (expression != null) {
            configurer.secured(expression);
        }
        if (attributes != null) {
            configurer.secured(attributes, match);
        }

        buildActions(configurer, actions, props);

        if (guard != null) {
            configurer.guard(new DelegateGuard<>(guard, props));
        }
    }

    protected void buildActions(
        TransitionConfigurer<T, S, ScenarioEvent> builder,
        List<Pair<Action<S>, Action<S>>> actions, Map<String, Object> props
    ) {
        for (Pair<Action<S>, Action<S>> pair : actions) {
            DelegateAction<S> action = new DelegateAction<>(pair.getLeft(), props);
            if (pair.getRight() != null) {
                DelegateAction<S> error = new DelegateAction<>(pair.getRight(), props);
                builder.action(action, error);
            } else {
                builder.action(action);
            }
        }
    }

    protected abstract StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception;
}
