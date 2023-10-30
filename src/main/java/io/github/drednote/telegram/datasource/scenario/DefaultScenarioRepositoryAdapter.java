package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.utils.Assert;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@SuppressWarnings("unchecked")
public class DefaultScenarioRepositoryAdapter implements ScenarioRepositoryAdapter {

  @Nullable
  private final ScenarioRepository repository;
  private final ScenarioRepositoryAdapter fallback;

  public DefaultScenarioRepositoryAdapter(
      ObjectProvider<ScenarioRepository<? extends PersistScenario>> repositoryProvider,
      ScenarioRepositoryAdapter fallback
  ) {
    Assert.required(repositoryProvider, "ScenarioRepository provider");
    Assert.required(fallback, "Fallback ScenarioRepositoryAdapter");

    this.repository = repositoryProvider.getIfAvailable();
    this.fallback = fallback;
  }

  @Nullable
  @Override
  public PersistScenario findScenario(Long chatId) {
    if (repository == null) {
      return fallback.findScenario(chatId);
    }
    return (PersistScenario) repository.findById(chatId).orElse(null);
  }

  @Override
  public void saveScenario(PersistScenario persistScenario) {
    if (repository == null) {
      fallback.saveScenario(persistScenario);
    } else {
      repository.save(persistScenario);
    }
  }

  @Override
  public void deleteScenario(Long chatId) {
    if (repository == null) {
      fallback.deleteScenario(chatId);
    } else {
      repository.deleteById(chatId);
    }
  }

  @NonNull
  @Override
  public Class<? extends PersistScenario> getClazz() {
    if (repository == null) {
      return fallback.getClazz();
    }
    ResolvableType generic = ResolvableType
        .forClass(ScenarioRepository.class, this.repository.getClass())
        .getGeneric(0);
    Class<?> resolve = generic.resolve();
    if (resolve == null) {
      throw new IllegalStateException(
          "Cannot resolve Scenario entity class from CrudRepository generic types");
    }
    return ((Class<? extends PersistScenario>) resolve);
  }
}
