package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioMonitor;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioPersister;

@BetaApi
public sealed interface ScenarioMachineConfigurer permits ScenarioMachineConfigurerImpl {

  ScenarioConfigurer scenario();

  ScenarioMachineConfigurer withPersister(ScenarioPersister persister);
  ScenarioMachineConfigurer withMonitor(ScenarioMonitor monitor);
}
