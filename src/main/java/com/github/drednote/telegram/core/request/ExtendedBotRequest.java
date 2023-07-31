package com.github.drednote.telegram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import com.github.drednote.telegram.updatehandler.scenario.Scenario;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface ExtendedBotRequest extends BotRequest {

  void setScenario(Scenario scenario);

  void setResponse(HandlerResponse response);

  @Nullable
  RequestHandler getRequestHandler();

  void setRequestHandler(RequestHandler requestHandler);

  @NonNull
  ObjectMapper getObjectMapper();

  void setPermission(Permission permission);
}
