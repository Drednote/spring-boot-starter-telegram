package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.ActionExecutor;
import java.util.function.Consumer;

@BetaApi
public sealed interface StepConfigurer permits StepConfigurerImpl {

  StepConfigurer name(String name);

  StepConfigurer pattern(UpdateRequestMapping pattern); // todo change on configurer and array

  StepConfigurer action(ActionExecutor action);

  StepConfigurer child(Consumer<StepConfigurer> consumer);

  StepConfigurer refToStep(String name); // todo change on configurer

  StepConfigurer next();

  ScenarioMachineConfigurer and();
}
