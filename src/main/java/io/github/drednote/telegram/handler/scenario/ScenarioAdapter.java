package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioMachineConfigurer;

@BetaApi
@FunctionalInterface
public interface ScenarioAdapter {

  void onConfigure(ScenarioMachineConfigurer configurer);
}
