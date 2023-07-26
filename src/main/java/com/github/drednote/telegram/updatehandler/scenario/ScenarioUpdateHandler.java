package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.utils.ResponseSetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class ScenarioUpdateHandler implements UpdateHandler {

  private final ScenarioPersister persister;
  private final ScenarioFactory scenarioFactory;

  public ScenarioUpdateHandler(ScenarioMachineContainer container) {
    this.persister = container.getScenarioPersister();
    this.scenarioFactory = container;
  }

  @Override
  public void onUpdate(UpdateRequest request) throws Exception {
    Long chatId = request.getChatId();
    Scenario scenario = scenarioFactory.createInitial(chatId);
    persister.restore(scenario);
    Result result = scenario.makeStep(request);
    if (result.made()) {
      ResponseSetter.setResponse(request, result.response());
      persister.persist(scenario);
    }
  }
}
