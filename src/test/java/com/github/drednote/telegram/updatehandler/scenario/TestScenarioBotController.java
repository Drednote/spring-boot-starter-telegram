package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.RequestType;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.scenario.annotation.ScenarioBotController;
import com.github.drednote.telegram.updatehandler.scenario.annotation.ScenarioBotRequest;

@ScenarioBotController(value = "/testCommand", type = RequestType.COMMAND)
public class TestScenarioBotController {

  @ScenarioBotRequest
  public HandlerResponse handleFirst(UpdateRequest request) {
    return new EmptyHandlerResponse();
  }
}
