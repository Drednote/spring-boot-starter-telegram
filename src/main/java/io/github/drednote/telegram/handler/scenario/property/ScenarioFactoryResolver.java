package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

@BetaApi
public interface ScenarioFactoryResolver {

    @Nullable
    HandlerMethod resolveAction(String name);
}
