package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.updatehandler.scenario.ActionExecutor;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import java.util.LinkedList;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@BetaApi
public record StepDefinition(
    @Nullable String name, @Nullable List<TelegramRequestMapping> pattern,
    @NonNull ActionExecutor action,
    @NonNull LinkedList<StepDefinition> steps,
    @Nullable String refToStep
) {}
