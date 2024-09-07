package io.github.drednote.telegram.handler.scenario_deprecated;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.datasource.scenario_deprecated.InMemoryScenarioRepositoryAdapter;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class InMemoryScenarioPersisterTest {

  DefaultScenarioPersister persister;
  InMemoryScenarioRepositoryAdapter adapter;

  {
    this.adapter = new InMemoryScenarioRepositoryAdapter();
    this.persister = new DefaultScenarioPersister(adapter);
  }

  @Test
  void shouldRemoveAfterFinished() throws IOException {
    ScenarioImpl scenario = new ScenarioImpl(1L, null, null);
    scenario.name = "testName";
    scenario.finished = false;
    persister.persist(scenario);
    assertThat(adapter.findScenario(1L)).isNotNull();

    scenario.finished = true;
    persister.persist(scenario);

    assertThat(adapter.findScenario(1L)).isNull();
  }

  @Test
  void shouldCorrectRestore() throws IOException {
    ScenarioImpl scenario = new ScenarioImpl(1L, null, null);
    scenario.step = new StepImpl(scenario, request -> null, "scenarioName");
    scenario.name = "testName";
    persister.persist(scenario);
    ScenarioImpl restoringScenario = new ScenarioImpl(1L, null, null);
    persister.restore(restoringScenario);
    assertThat(restoringScenario.name).isEqualTo(scenario.name);
    assertThat(restoringScenario.step).isEqualTo(scenario.step);
  }
}