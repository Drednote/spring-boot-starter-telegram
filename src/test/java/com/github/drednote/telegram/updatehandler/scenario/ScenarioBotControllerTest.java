package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.UpdateUtils;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class ScenarioBotControllerTest {

  UpdateHandler updateHandler;

  @BeforeEach
  void setUp() {
    Scenario scenario = new ScenarioBuilder()
        .startCommand("/start")
        .withCancel().command("/cancel")
        .action(updateRequest -> {
          log.info("cancel -> " + updateRequest.getChatId());
          return null;
        }).finish()

        .step()
        .withRequest(updateRequest -> {
          log.info("first -> " + updateRequest.getChatId());
          return null;
        })
        .next()

        .step()
        .withRequest(updateRequest -> {
          log.info("last -> " + updateRequest.getChatId());
          return null;
        })
        .next().build();
    updateHandler = new ScenarioUpdateHandler(List.of(scenario));
  }

  @Test
  void name() throws Exception {
    updateHandler.onUpdate(new UpdateRequest(UpdateUtils.createCommandUpdate("/start"), null));
    updateHandler.onUpdate(new UpdateRequest(UpdateUtils.createCommandUpdate("/cancel"), null));

  }
}