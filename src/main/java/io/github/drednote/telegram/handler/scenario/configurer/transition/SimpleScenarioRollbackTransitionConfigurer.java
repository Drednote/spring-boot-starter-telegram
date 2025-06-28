package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.machine.DelegateGuard;
import io.github.drednote.telegram.handler.scenario.machine.Guard;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class SimpleScenarioRollbackTransitionConfigurer<S>
    extends
    AbstractScenarioBaseTransitionConfigurer<ExternalTransitionConfigurer<S, ScenarioEvent>, ScenarioRollbackTransitionConfigurer<S>, S>
    implements ScenarioRollbackTransitionConfigurer<S> {

    private final ExternalTransitionConfigurer<S, ScenarioEvent> configurer;

    private final List<Pair<Action<S>, Action<S>>> rollbackActions = new ArrayList<>();
    @Nullable
    private Guard<S> rollbackGuard;
    @Nullable
    private TelegramRequest rollbackRequest;
    private Map<String, Object> rollbackProps = new HashMap<>();

    public SimpleScenarioRollbackTransitionConfigurer(
        ScenarioBuilder<S> builder,
        ExternalTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.configurer = configurer;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> target(S target) {
        this.target = target;
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action) {
        this.rollbackActions.add(Pair.of(action, null));
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action, Action<S> error) {
        this.rollbackActions.add(Pair.of(action, error));
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackTelegramRequest(TelegramRequest telegramRequest) {
        this.rollbackRequest = telegramRequest;
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackGuard(Guard<S> guard) {
        this.rollbackGuard = guard;
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackProps(Map<String, Object> rollbackProps) {
        this.rollbackProps = rollbackProps;
        return this;
    }

    @Override
    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        Assert.required(rollbackRequest, "RollbackTelegramRequest");

        preBuild(configurer);

        if (target != null) {
            configurer.target(target);
        }

        var rollback = configurer.and().withExternal();

        if (target != null) {
            rollback.source(target);
        }
        if (source != null) {
            rollback.target(source);
        }
        if (request != null) {
            rollback.event(new ScenarioEvent(rollbackRequest));
        }

        buildActions(rollback, rollbackActions, rollbackProps);

        if (rollbackGuard != null) {
            rollback.guard(new DelegateGuard<>(rollbackGuard, rollbackProps));
        }

        return rollback.and();
    }
}
