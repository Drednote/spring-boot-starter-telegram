package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.core.ActionExecutor;
import java.util.LinkedList;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record ScenarioDefinition(
    @NonNull String startCommand, @Nullable String name,
    @NonNull @ToString.Exclude ActionExecutor action,
    @NonNull LinkedList<StepDefinition> steps
) {}
