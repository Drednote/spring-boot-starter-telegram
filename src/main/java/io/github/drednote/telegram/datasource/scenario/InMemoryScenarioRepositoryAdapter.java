package io.github.drednote.telegram.datasource.scenario;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class InMemoryScenarioRepositoryAdapter implements ScenarioRepositoryAdapter {

  private final Map<Long, PersistScenario> pool = new ConcurrentHashMap<>();

  @Nullable
  @Override
  public PersistScenario findScenario(@NonNull Long chatId) {
    return pool.get(chatId);
  }

  @Override
  public void saveScenario(@NonNull PersistScenario persistScenario) {
    pool.put(persistScenario.getId(), persistScenario);
  }

  @Override
  public void deleteScenario(@NonNull Long chatId) {
    pool.remove(chatId);
  }

  @NonNull
  @Override
  public Class<? extends PersistScenario> getClazz() {
    return InMemoryPersistScenario.class;
  }

  @Getter
  @Setter
  public static class InMemoryPersistScenario implements PersistScenario {

    private Long id;
    private String name;
    private String stepName;
    private byte[] context;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      InMemoryPersistScenario that = (InMemoryPersistScenario) o;
      return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }
}
