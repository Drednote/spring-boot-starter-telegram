package io.github.drednote.telegram.updatehandler.scenario.configurer;

import io.github.drednote.telegram.updatehandler.scenario.ScenarioMonitor;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioPersister;
import io.github.drednote.telegram.utils.Assert;
import java.util.LinkedList;
import lombok.Getter;

@Getter
public final class ScenarioMachineConfigurerImpl implements ScenarioMachineConfigurer {

  private ScenarioPersister persister;
  private ScenarioMonitor monitor;
  private final LinkedList<ScenarioDefinition> scenarios = new LinkedList<>();

  @Override
  public ScenarioMachineConfigurer withPersister(ScenarioPersister persister) {
    Assert.notNull(persister, "persister");
    this.persister = persister;
    return this;
  }

  @Override
  public ScenarioMachineConfigurer withMonitor(ScenarioMonitor monitor) {
    Assert.notNull(monitor, "monitor");
    this.monitor = monitor;
    return this;
  }

  @Override
  public ScenarioConfigurer scenario() {
    return new ScenarioConfigurerImpl(scenarios::addLast, this);
  }
}