package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;

@BetaApi
public record ResultImpl(boolean isMade, Object response) implements Result {}
