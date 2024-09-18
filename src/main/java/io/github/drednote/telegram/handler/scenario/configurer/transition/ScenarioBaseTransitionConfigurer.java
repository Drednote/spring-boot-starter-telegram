package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;

public interface ScenarioBaseTransitionConfigurer<C extends ScenarioBaseTransitionConfigurer<C, S>, S> {

    C source(S source);

    C target(S target);

    C action(Action<S> action);

    C telegramRequest(TelegramRequest telegramRequest);

    C overrideGlobalScenarioId();

    ScenarioTransitionConfigurer<S> and();
}
