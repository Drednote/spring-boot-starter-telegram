package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;

public interface ScenarioRollbackTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioRollbackTransitionConfigurer<S>, S> {

    ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action);

    ScenarioRollbackTransitionConfigurer<S> rollbackTelegramRequest(TelegramRequest telegramRequest);
}
