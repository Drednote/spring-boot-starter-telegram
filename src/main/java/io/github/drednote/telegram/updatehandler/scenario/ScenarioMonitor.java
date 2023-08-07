package io.github.drednote.telegram.updatehandler.scenario;

public interface ScenarioMonitor {

  void madeStep(Step from, Step to);
}
