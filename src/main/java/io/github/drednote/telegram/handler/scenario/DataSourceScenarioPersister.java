package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.datasource.ScenarioDB;
import io.github.drednote.telegram.datasource.kryo.ScenarioContext;
import io.github.drednote.telegram.datasource.kryo.ScenarioMachineSerializationService;
import io.github.drednote.telegram.handler.scenario.ScenarioImpl.Node;
import io.github.drednote.telegram.utils.Assert;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;

@BetaApi
public class DataSourceScenarioPersister implements ScenarioPersister {

  private final CrudRepository<? extends ScenarioDB, Long> repository;
  private final ScenarioMachineSerializationService serializationService;
  private final Class<? extends ScenarioDB> clazz;

  public DataSourceScenarioPersister(DataSourceAdapter dataSourceAdapter) {
    Assert.required(dataSourceAdapter, "DataSourceAdapter");
    this.serializationService = new ScenarioMachineSerializationService();
    this.repository = dataSourceAdapter.scenarioRepository();
    ResolvableType generic = ResolvableType
        .forClass(CrudRepository.class, this.repository.getClass())
        .getGeneric(0);
    Class<?> resolve = generic.resolve();
    if (resolve == null) {
      throw new IllegalStateException(
          "Cannot resolve Scenario entity class from CrudRepository generic types");
    }
    this.clazz = (Class<? extends ScenarioDB>) resolve;
  }

  @Override
  public void persist(Scenario scenario) throws IOException {
    validate(scenario);
    if (scenario.isFinished()) {
      repository.deleteById(scenario.getId());
      // todo add history
    } else {
      byte[] bytes = serializationService.serialize(ScenarioContext.from(scenario));
      repository.save(createEntityInstance(scenario, bytes));
    }
  }

  @Override
  public void restore(Scenario scenario) {
    validate(scenario);
    repository.findById(scenario.getId()).ifPresent(scenarioDB -> {
      ScenarioContext context = serializationService.deserialize(scenarioDB.getContext());
      if (scenario instanceof ScenarioImpl impl) {
        doRestore(impl, context);
      } else {
        throw new IllegalStateException(
            "Unknown scenario implementation %s".formatted(scenario.getClass()));
      }
    });
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
  private <K extends ScenarioDB> K createEntityInstance(Scenario scenario, byte[] bytes) {
    try {
      ScenarioDB scenarioDB = clazz.getDeclaredConstructor().newInstance();
      scenarioDB.setId(scenario.getId());
      scenarioDB.setName(scenario.getName());
      scenarioDB.setStepName(scenario.getCurrentStep().getName());
      scenarioDB.setContext(bytes);
      return (K) scenarioDB;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new IllegalStateException(
          "Cannot persist scenario to db, consider create public constructor without arguments on database entity");
    }
  }
}
