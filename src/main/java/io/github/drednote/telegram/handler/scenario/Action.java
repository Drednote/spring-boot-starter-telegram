package io.github.drednote.telegram.handler.scenario;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface Action<S> {

    @Nullable
    Object execute(ActionContext<S> context);
}
