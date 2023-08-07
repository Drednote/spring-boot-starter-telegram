package io.github.drednote.telegram.updatehandler.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InMemoryScenarioPersisterTest {

  InMemoryScenarioPersister persister = new InMemoryScenarioPersister();

  @Test
  void shouldRemoveAfterFinished() {
    ScenarioImpl scenario = new ScenarioImpl(1L, null, null);
    scenario.finished = false;
    persister.persist(scenario);
    assertThat(persister.map).containsKey(1L);

    scenario.finished = true;
    persister.persist(scenario);

    assertThat(persister.map).doesNotContainKey(1L);
  }

  @Test
  void shouldCorrectRestore() {
    ScenarioImpl scenario = new ScenarioImpl(1L, null, null);
    scenario.step = null;
    scenario.name = "testName";
    persister.persist(scenario);
    ScenarioImpl restoringScenario = new ScenarioImpl(1L, null, null);
    persister.restore(restoringScenario);
    assertThat(restoringScenario.name).isEqualTo(scenario.name);
    assertThat(restoringScenario.step).isEqualTo(scenario.step);
  }
}