package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.matcher.RequestMatcher;
import io.github.drednote.telegram.handler.scenario.Action;

public sealed interface State<S> extends RequestMatcher, Action permits AbstractState {

    S getId();
}
