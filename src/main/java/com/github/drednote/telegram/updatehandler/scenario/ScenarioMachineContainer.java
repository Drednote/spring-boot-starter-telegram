package com.github.drednote.telegram.updatehandler.scenario;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.github.drednote.telegram.updatehandler.UpdateHandlerProperties;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioImpl.Node;
import com.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class ScenarioMachineContainer implements ScenarioFactory {

  private final List<Node> scenarios;
  private final Map<String, Node> flatNodes;
  @Getter
  private final ScenarioPersister scenarioPersister;
  private final UpdateHandlerProperties properties;

  public ScenarioMachineContainer(
      ScenarioMachineConfigurerImpl configurer, UpdateHandlerProperties properties
  ) {
    this.properties = properties;
    ScenarioNodeBuilder builder = new ScenarioNodeBuilder(configurer.getScenarios());
    this.scenarios = builder.getScenarios();
    this.flatNodes = builder.getFlatNodes();
    this.scenarioPersister = firstNonNull(configurer.getPersister(),
        new InMemoryScenarioPersister());
  }

  @Override
  public Scenario createInitial(Long chatId) {
    return new ScenarioImpl(chatId, new ArrayList<>(scenarios), new HashMap<>(flatNodes), properties.getScenarioLockMs());
  }
}
