package io.github.drednote.telegram.updatehandler.scenario;

public interface ScenarioFactory {

  Scenario createInitial(Long chatId);
}
