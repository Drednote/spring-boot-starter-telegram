package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurer;

@FunctionalInterface
public interface ScenarioAdapter {

  void onConfigure(ScenarioMachineConfigurer configurer);
}
