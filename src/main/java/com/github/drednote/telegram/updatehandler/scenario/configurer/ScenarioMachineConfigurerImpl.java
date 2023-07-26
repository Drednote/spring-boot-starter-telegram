package com.github.drednote.telegram.updatehandler.scenario.configurer;

import com.github.drednote.telegram.updatehandler.scenario.ScenarioPersister;
import com.github.drednote.telegram.utils.Assert;
import java.util.LinkedList;
import lombok.Getter;

public final class ScenarioMachineConfigurerImpl implements ScenarioMachineConfigurer {

  @Getter
  private ScenarioPersister persister;
  @Getter
  private final LinkedList<ScenarioDefinition> scenarios = new LinkedList<>();

  @Override
  public ScenarioMachineConfigurer withPersister(ScenarioPersister persister) {
    Assert.notNull(persister, "persister");
    this.persister = persister;
    return this;
  }

  @Override
  public ScenarioConfigurer scenario() {
    return new ScenarioConfigurerImpl(scenarios::addLast, this);
  }
}
