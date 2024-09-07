package io.github.drednote.telegram.handler.scenario;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface Action {

    @Nullable
    Object execute(ActionContext context);
}
