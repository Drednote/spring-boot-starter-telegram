package io.github.drednote.telegram.handler.scenario.property;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public interface ScenarioFactoryResolver {

    @Nullable
    HandlerMethod resolveAction(String name);
}
