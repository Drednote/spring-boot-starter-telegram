package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;

public interface ScenarioBaseTransitionConfigurer<T extends ScenarioBaseTransitionConfigurer<T, S>, S> {

    T source(S source);

    T target(S target);

    T action(Action action);

    T telegramRequest(TelegramRequest telegramRequest);

    ScenarioTransitionConfigurer<S> and();
}
