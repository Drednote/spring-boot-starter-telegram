package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.scenario_deprecated.PersistScenario;
import io.github.drednote.telegram.datasource.scenario_deprecated.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.kryo.ScenarioContext;
import io.github.drednote.telegram.datasource.kryo.ScenarioMachineSerializationService;
import io.github.drednote.telegram.handler.scenario_deprecated.ScenarioImpl.Node;
import io.github.drednote.telegram.utils.Assert;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@BetaApi
public class DefaultScenarioPersister implements ScenarioPersister {

  private final ScenarioMachineSerializationService serializationService;
  private final Class<? extends PersistScenario> clazz;
  private final ScenarioRepositoryAdapter adapter;

  public DefaultScenarioPersister(ScenarioRepositoryAdapter adapter) {
    Assert.required(adapter, "ScenarioRepositoryAdapter");
    this.serializationService = new ScenarioMachineSerializationService();
    this.adapter = adapter;
    this.clazz = adapter.getClazz();
  }

  @Override
  public void persist(Scenario scenario) throws IOException {
    validate(scenario);
    if (scenario.isFinished()) {
      adapter.deleteScenario(scenario.getId());
      // todo add history
    } else {
      byte[] bytes = serializationService.serialize(ScenarioContext.from(scenario));
      adapter.saveScenario(createEntityInstance(scenario, bytes));
    }
  }

  @Override
  public void restore(Scenario scenario) {
    validate(scenario);
    PersistScenario scenarioDB = adapter.findScenario(scenario.getId());
    if (scenarioDB != null) {
      ScenarioContext context = serializationService.deserialize(scenarioDB.getContext());
      if (scenario instanceof ScenarioImpl impl) {
        doRestore(impl, context);
      } else {
        throw new IllegalStateException(
            "Unknown scenario implementation %s".formatted(scenario.getClass()));
      }
    }
  }

  private static void validate(Scenario scenario) {
    Assert.notNull(scenario, "scenario");
    Assert.notNull(scenario.getId(), "scenario id");
  }

  private void doRestore(ScenarioImpl scenario, ScenarioContext context) {
    scenario.name = context.getName();
    scenario.finished = context.isFinished();

    String stepName = context.getStepName();
    Node node = scenario.nodes.get(stepName);
    scenario.checkNextStep(node, stepName, "persisted");
    scenario.step = new StepImpl(scenario, node.action, stepName);
  }

  @SuppressWarnings("unchecked")
  private <K extends PersistScenario> K createEntityInstance(Scenario scenario, byte[] bytes) {
    try {
      PersistScenario persistScenario = clazz.getDeclaredConstructor().newInstance();
      persistScenario.setId(scenario.getId());
      persistScenario.setName(scenario.getName());
      persistScenario.setStepName(scenario.getCurrentStep().getName());
      persistScenario.setContext(bytes);
      return (K) persistScenario;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new IllegalStateException(
          "Cannot persist scenario to db, consider create public constructor without arguments on database entity");
    }
  }
}
