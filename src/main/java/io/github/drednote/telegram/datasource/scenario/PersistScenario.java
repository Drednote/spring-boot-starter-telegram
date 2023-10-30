package io.github.drednote.telegram.datasource.scenario;

import org.springframework.lang.Nullable;

public interface PersistScenario {

  Long getId();

  @Nullable
  String getName();

  @Nullable
  String getStepName();

  @Nullable
  byte[] getContext();

  void setId(Long id);

  void setName(@Nullable String name);

  void setStepName(@Nullable String stepName);

  void setContext(@Nullable byte[] bytes);
}
