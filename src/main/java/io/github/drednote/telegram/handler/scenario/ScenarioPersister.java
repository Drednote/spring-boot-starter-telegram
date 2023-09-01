package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import java.io.IOException;

@BetaApi
public interface ScenarioPersister {

  /**
   * @implSpec must remove from a datasource scenario if {@link Scenario#isFinished()} return true
   * and copy it to history
   */
  void persist(Scenario scenario) throws IOException;

  void restore(Scenario scenario);
}
