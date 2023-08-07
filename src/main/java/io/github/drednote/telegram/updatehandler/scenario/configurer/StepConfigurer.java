package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.core.ActionExecutor;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import java.util.function.Consumer;

public sealed interface StepConfigurer permits StepConfigurerImpl {

  StepConfigurer name(String name);

  StepConfigurer pattern(TelegramRequestMapping pattern); // todo change on configurer and array

  StepConfigurer action(ActionExecutor action);

  StepConfigurer child(Consumer<StepConfigurer> consumer);

  StepConfigurer refToStep(String name); // todo change on configurer

  StepConfigurer next();

  ScenarioMachineConfigurer and();
}
