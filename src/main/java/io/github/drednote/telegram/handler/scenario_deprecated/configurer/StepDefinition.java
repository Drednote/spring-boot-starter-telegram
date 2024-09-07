package io.github.drednote.telegram.handler.scenario_deprecated.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario_deprecated.ActionExecutor;
import java.util.LinkedList;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@BetaApi
public record StepDefinition(
    @Nullable String name, @Nullable List<UpdateRequestMapping> pattern,
    @NonNull ActionExecutor action,
    @NonNull LinkedList<StepDefinition> steps,
    @Nullable String refToStep
) {}
