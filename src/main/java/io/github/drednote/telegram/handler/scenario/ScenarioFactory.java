package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;

@BetaApi
public interface ScenarioFactory {

  Scenario createInitial(Long chatId);
}
