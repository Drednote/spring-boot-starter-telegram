package io.github.drednote.telegram.handler.scenario_deprecated.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.handler.scenario_deprecated.ActionExecutor;
import java.util.LinkedList;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@BetaApi
public record ScenarioDefinition(
    @NonNull String startCommand, @Nullable String name,
    @NonNull @ToString.Exclude ActionExecutor action,
    @NonNull LinkedList<StepDefinition> steps
) {}
