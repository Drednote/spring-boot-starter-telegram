package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;

public class SimpleScenarioRollbackTransitionConfigurer<S>
    extends SimpleScenarioBaseTransitionConfigurer<ScenarioRollbackTransitionConfigurer<S>, S>
    implements ScenarioRollbackTransitionConfigurer<S> {

    private final List<Action<S>> rollbackActions = new ArrayList<>();
    private TelegramRequest rollbackTelegramRequest;

    public SimpleScenarioRollbackTransitionConfigurer(ScenarioBuilder<S> builder) {
        super(builder);
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action) {
        this.rollbackActions.add(action);
        return this;
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> rollbackTelegramRequest(TelegramRequest telegramRequest) {
        this.rollbackTelegramRequest = telegramRequest;
        return this;
    }

    @Override
    protected void beforeAnd(TransitionData<S> data) {
        Assert.required(rollbackTelegramRequest, "RollbackTelegramRequest");

        TransitionData<S> transition = new TransitionData<>(
            target, source, rollbackActions, rollbackTelegramRequest, false
        );
        builder.addTransition(transition);
    }
}
