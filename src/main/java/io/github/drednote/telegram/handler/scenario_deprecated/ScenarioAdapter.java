package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.handler.scenario_deprecated.configurer.ScenarioMachineConfigurer;

@BetaApi
@FunctionalInterface
public interface ScenarioAdapter {

  void onConfigure(ScenarioMachineConfigurer configurer);
}
