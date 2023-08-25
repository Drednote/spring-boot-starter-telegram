package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.updatehandler.scenario.ActionExecutor;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StepConfigurerImpl implements StepConfigurer {

  private final Consumer<StepDefinition> stepDefinitionConsumer;
  private final ScenarioConfigurer parent;
  private final LinkedList<StepDefinition> steps = new LinkedList<>();
  private boolean finished = false;
  private String name;
  private String refToStep;
  private final List<TelegramRequestMapping> pattern = new ArrayList<>();
  private ActionExecutor action;

  @Override
  public StepConfigurer name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public StepConfigurer pattern(TelegramRequestMapping pattern) {
    this.pattern.add(pattern);
    return this;
  }

  @Override
  public StepConfigurer action(ActionExecutor action) {
    this.action = action;
    return this;
  }

  @Override
  public StepConfigurer child(Consumer<StepConfigurer> consumer) {
    Assert.notNull(consumer, "child consumer");
    StepConfigurerImpl configurer = new StepConfigurerImpl(steps::addLast, parent);
    consumer.accept(configurer);
    return this;
  }

  @Override
  public StepConfigurer refToStep(String refToStep) {
    this.refToStep = refToStep;
    return this;
  }

  @Override
  public StepConfigurer next() {
    addStep();
    return new StepConfigurerImpl(stepDefinitionConsumer, parent);
  }

  @Override
  public ScenarioMachineConfigurer and() {
    addStep();
    return parent.and();
  }

  void addStep() {
    Assert.specify(action, "action");
    stepDefinitionConsumer.accept(new StepDefinition(name, pattern, action, steps, refToStep));
    this.finished = true;
  }
}
