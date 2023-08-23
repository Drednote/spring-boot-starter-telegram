package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.updatehandler.scenario.ActionExecutor;

public sealed interface ScenarioConfigurer permits ScenarioConfigurerImpl {

  ScenarioConfigurer startCommand(String command);

  ScenarioConfigurer name(String name);

  ScenarioConfigurer action(ActionExecutor action);

  StepConfigurer step();

  ScenarioMachineConfigurer and();
}
