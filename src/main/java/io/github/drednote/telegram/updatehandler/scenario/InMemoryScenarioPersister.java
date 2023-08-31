package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@BetaApi
public class InMemoryScenarioPersister implements ScenarioPersister {

  final Map<Long, Scenario> map = new ConcurrentHashMap<>();

  @Override
  public void persist(Scenario scenario) {
    if (scenario.isFinished()) {
      map.remove(scenario.getId());
    } else {
      map.put(scenario.getId(), scenario);
    }
  }

  @Override
  public void restore(Scenario scenario) {
    Scenario saved = map.get(scenario.getId());
    if (saved != null && !saved.isFinished()) {
      ScenarioImpl savedImpl = (ScenarioImpl) saved;
      ScenarioImpl impl = (ScenarioImpl) scenario;
      impl.finished = savedImpl.finished;
      impl.name = savedImpl.name;
      impl.step = savedImpl.step;
    }
  }
}
