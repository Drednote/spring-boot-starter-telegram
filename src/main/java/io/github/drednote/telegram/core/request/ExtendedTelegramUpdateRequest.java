package io.github.drednote.telegram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.updatehandler.HandlerResponse;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.scenario.Scenario;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface ExtendedTelegramUpdateRequest extends TelegramUpdateRequest {

  void setScenario(Scenario scenario);

  void setResponse(HandlerResponse response);

  @Nullable
  RequestHandler getRequestHandler();

  void setRequestHandler(RequestHandler requestHandler);

  @NonNull
  ObjectMapper getObjectMapper();

  void setPermission(Permission permission);
}
