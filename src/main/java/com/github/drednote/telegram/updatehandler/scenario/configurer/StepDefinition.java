package com.github.drednote.telegram.updatehandler.scenario.configurer;

import com.github.drednote.telegram.core.ActionExecutor;
import com.github.drednote.telegram.core.RequestMappingInfo;
import java.util.LinkedList;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record StepDefinition(
    @Nullable String name, @Nullable List<RequestMappingInfo> pattern,
    @NonNull ActionExecutor action,
    @NonNull LinkedList<StepDefinition> steps,
    @Nullable String refToStep
) {}
