package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.core.ActionExecutor;
import io.github.drednote.telegram.utils.Assert;
import java.util.LinkedList;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public final class ScenarioConfigurerImpl implements ScenarioConfigurer {

  private final Consumer<ScenarioDefinition> scenarioConsumer;
  private final ScenarioMachineConfigurer parent;
  private String startCommand;
  @Nullable
  private String name;
  private ActionExecutor action;
  private final LinkedList<StepDefinition> steps = new LinkedList<>();

  @Override
  public ScenarioConfigurer startCommand(String command) {
    this.startCommand = command;
    return this;
  }

  @Override
  public ScenarioConfigurer name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public ScenarioConfigurer action(ActionExecutor action) {
    this.action = action;
    return this;
  }

  @Override
  public StepConfigurer step() {
    return new StepConfigurerImpl(steps::addLast, this);
  }

  @Override
  public ScenarioMachineConfigurer and() {
    Assert.specify(startCommand, "startCommand");
    Assert.specify(action, "action");
    Assert.specify(steps, "steps");

    scenarioConsumer.accept(new ScenarioDefinition(startCommand, name, action, steps));
    return parent;
  }
}
