package io.github.drednote.telegram.handler.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.drednote.telegram.datasource.scenario.DefaultScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.InMemoryScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.ScenarioEntity;
import io.github.drednote.telegram.support.MockObjectProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

class DefaultScenarioPersisterTest {

  DefaultScenarioPersister persister;
  Repo repository;

  @BeforeEach
  void setUp() {
    repository = new Repo();
    persister = new DefaultScenarioPersister(
        new DefaultScenarioRepositoryAdapter(new MockObjectProvider<>(repository),
            new InMemoryScenarioRepositoryAdapter()));
  }


  @Test
  void shouldCallRepository() throws IOException {
    ScenarioImpl scenario = new ScenarioImpl(1L, List.of(), Map.of());
    scenario.name = "1";
    scenario.step = new StepImpl(scenario, r -> null, "2");
    persister.persist(scenario);
    assertThat(repository.count).isOne();
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

  static class Repo implements ScenarioRepository<ScenarioEntity> {

    int count = 0;

    @Override
    public <S extends ScenarioEntity> S save(S entity) {
      count++;
      return entity;
    }

    @Override
    public <S extends ScenarioEntity> Iterable<S> saveAll(Iterable<S> entities) {
      return null;
    }

    @Override
    public Optional<ScenarioEntity> findById(Long aLong) {
      return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
      return false;
    }

    @Override
    public Iterable<ScenarioEntity> findAll() {
      return null;
    }

    @Override
    public Iterable<ScenarioEntity> findAllById(Iterable<Long> longs) {
      return null;
    }

    @Override
    public long count() {
      return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(ScenarioEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends ScenarioEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }


  }
}