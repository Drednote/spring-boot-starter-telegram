package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;

@BetaApi
public interface ScenarioMonitor {

  void madeStep(Step from, Step to);
}