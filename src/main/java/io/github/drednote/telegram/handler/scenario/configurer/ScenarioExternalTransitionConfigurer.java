package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;

public interface ScenarioExternalTransitionConfigurer<S> {

    ScenarioExternalTransitionConfigurer<S> source(S source);

    ScenarioExternalTransitionConfigurer<S> target(S target);

    ScenarioExternalTransitionConfigurer<S> action(Action action);

    ScenarioExternalTransitionConfigurer<S> telegramRequest(TelegramRequest telegramRequest);

    ScenarioTransitionConfigurer<S> and();
}
