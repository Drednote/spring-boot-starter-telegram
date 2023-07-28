package com.github.drednote.telegram.updatehandler.scenario;

import java.io.IOException;

public interface ScenarioPersister {

  /**
   * @implSpec must remove from a datasource scenario if {@link Scenario#isFinished()} return true
   * and copy it to history
   */
  void persist(Scenario scenario) throws IOException;

  void restore(Scenario scenario);
}
