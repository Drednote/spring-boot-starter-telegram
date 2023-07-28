package com.github.drednote.telegram.updatehandler.scenario;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import com.github.drednote.telegram.datasource.DataSourceAdapterImpl;
import com.github.drednote.telegram.datasource.jpa.ScenarioEntity;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.CrudRepository;

class DataSourceScenarioPersisterTest {

  DataSourceScenarioPersister persister;
  CrudRepository repository;

  @BeforeEach
  void setUp() {
    repository = Mockito.mock(CrudRepository.class);
    persister = new DataSourceScenarioPersister(
        new DataSourceAdapterImpl(null, repository, ScenarioEntity.class));
  }


  @Test
  void shouldCallRepository() throws IOException {
    ScenarioImpl scenario = new ScenarioImpl(1L, List.of(), Map.of());
    scenario.name = "1";
    scenario.step = new StepImpl(scenario, r -> null, "2");
    persister.persist(scenario);
    verify(repository).save(Mockito.any(ScenarioEntity.class));
  }

  @Test
  void shouldNotPassValidation() {
    assertThatThrownBy(() -> persister.persist(null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> persister.restore(null)).isInstanceOf(IllegalArgumentException.class);
    ScenarioImpl scenario = new ScenarioImpl(null, List.of(), Map.of());
    assertThatThrownBy(() -> persister.persist(scenario)).isInstanceOf(
        IllegalArgumentException.class);
    assertThatThrownBy(() -> persister.restore(scenario)).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void shouldFailIf() {
    assertThatThrownBy(() -> persister.persist(null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> persister.restore(null)).isInstanceOf(IllegalArgumentException.class);
    ScenarioImpl scenario = new ScenarioImpl(null, List.of(), Map.of());
    assertThatThrownBy(() -> persister.persist(scenario)).isInstanceOf(
        IllegalArgumentException.class);
    assertThatThrownBy(() -> persister.restore(scenario)).isInstanceOf(
        IllegalArgumentException.class);
  }

}