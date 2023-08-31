package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;

@BetaApi
public interface ScenarioMonitor {

  void madeStep(Step from, Step to);
}
