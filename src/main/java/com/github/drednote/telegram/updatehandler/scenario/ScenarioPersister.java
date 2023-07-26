package com.github.drednote.telegram.updatehandler.scenario;

public interface ScenarioPersister {

  /**
   * @implSpec must remove from a datasource scenario if {@link Scenario#isFinished()} return true
   */
  void persist(Scenario scenario);

  void restore(Scenario scenario);
}
