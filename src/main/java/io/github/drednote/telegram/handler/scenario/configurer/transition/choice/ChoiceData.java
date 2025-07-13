package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.guard.Guard;

/**
 * Represents data related to a decision point or choice within a scenario's state machine.
 *
 * @param <S> the type of state
 * @author Ivan Galushko
 */
public record ChoiceData<S>(
    S source, S target, Guard<S> guard, Action<S> action, Action<S> error
) {}