package io.github.drednote.telegram.datasource;

public interface ScenarioDB {

  Long getId();

  String getName();

  String getStepName();

  byte[] getContext();

  void setId(Long id);

  void setName(String name);

  void setStepName(String stepName);

  void setContext(byte[] bytes);
}
