package io.github.drednote.telegram.datasource.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import org.springframework.lang.Nullable;

@BetaApi
public interface ScenarioRepositoryAdapter extends DataSourceAdapter {

  @Nullable
  PersistScenario findScenario(Long chatId);

  void saveScenario(PersistScenario persistScenario);

  void deleteScenario(Long chatId);

  Class<? extends PersistScenario> getClazz();
}
