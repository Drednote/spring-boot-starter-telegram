package com.github.drednote.telegram.updatehandler.scenario;

public interface ScenarioFactory {

  Scenario createInitial(Long chatId);
}
