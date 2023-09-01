package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;

@BetaApi
public record ResultImpl(boolean isMade, Object response) implements Result {}
