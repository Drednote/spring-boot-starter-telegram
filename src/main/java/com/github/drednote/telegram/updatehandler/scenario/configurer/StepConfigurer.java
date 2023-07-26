package com.github.drednote.telegram.updatehandler.scenario.configurer;

import com.github.drednote.telegram.core.ActionExecutor;
import com.github.drednote.telegram.core.RequestMappingInfo;
import java.util.function.Consumer;

public sealed interface StepConfigurer permits StepConfigurerImpl {

  StepConfigurer name(String name);

  StepConfigurer pattern(RequestMappingInfo pattern); // todo change on configurer and array

  StepConfigurer action(ActionExecutor action);

  StepConfigurer child(Consumer<StepConfigurer> consumer);

  StepConfigurer refToStep(String name); // todo change on configurer

  StepConfigurer next();

  ScenarioMachineConfigurer and();
}
