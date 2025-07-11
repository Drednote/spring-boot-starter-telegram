package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.guard.Guard;

public record ChoiceData<S>(
    S source, S target, Guard<S> guard, Action<S> action, Action<S> error
) {}