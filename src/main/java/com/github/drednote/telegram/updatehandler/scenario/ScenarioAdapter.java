package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurer;

@FunctionalInterface
public interface ScenarioAdapter {

  void onConfigure(ScenarioMachineConfigurer configurer);
}
