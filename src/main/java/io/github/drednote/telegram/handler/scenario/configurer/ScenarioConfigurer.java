package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.handler.scenario.ActionExecutor;

@BetaApi
public sealed interface ScenarioConfigurer permits ScenarioConfigurerImpl {

  ScenarioConfigurer startCommand(String command);

  ScenarioConfigurer name(String name);

  ScenarioConfigurer action(ActionExecutor action);

  StepConfigurer step();

  ScenarioMachineConfigurer and();
}
