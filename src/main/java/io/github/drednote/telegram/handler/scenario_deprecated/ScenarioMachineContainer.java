package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.handler.scenario_deprecated.configurer.ScenarioMachineConfigurerImpl;
import io.github.drednote.telegram.handler.scenario_deprecated.ScenarioImpl.Node;
import io.github.drednote.telegram.handler.UpdateHandlerProperties;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@BetaApi
public class ScenarioMachineContainer implements ScenarioFactory {

  private final List<Node> scenarios;
  private final Map<String, Node> flatNodes;
  @Getter
  private final ScenarioPersister scenarioPersister;
  @Getter
  private final ScenarioMonitor monitor;
  private final UpdateHandlerProperties properties;

  public ScenarioMachineContainer(
      ScenarioMachineConfigurerImpl configurer, UpdateHandlerProperties properties
  ) {
    Assert.notNull(configurer, "configurer");
    Assert.notNull(properties, "properties");

    this.scenarioPersister = configurer.getPersister();
    this.monitor = configurer.getMonitor();

    this.properties = properties;
    ScenarioNodeBuilder builder = new ScenarioNodeBuilder(configurer.getScenarios());
    this.scenarios = builder.getScenarios();
    this.flatNodes = builder.getFlatNodes();
    Assert.notNull(scenarios, "root scenarios");
    Assert.notNull(flatNodes, "scenarios");
  }

  @Override
  public Scenario createInitial(Long chatId) {
    return doCreate(chatId);
  }

  private ScenarioImpl doCreate(Long chatId) {
    ScenarioImpl scenario = new ScenarioImpl(chatId, new ArrayList<>(scenarios),
        new HashMap<>(flatNodes));

    scenario.setLockMs(properties.getScenarioLockMs());
    scenario.setScenarioMonitor(monitor);

    return scenario;
  }
}
