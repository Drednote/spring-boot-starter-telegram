package io.github.drednote.telegram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.updatehandler.response.TelegramResponse;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.scenario.Scenario;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface TelegramUpdateRequest {

  //-----------getters for Update-----------

  @NonNull
  Integer getId();

  @NonNull
  Long getChatId();

  @NonNull
  RequestType getRequestType();

  /**
   * Present only if the request type is Message. Can contain multiple types
   * <p>
   * In case if a type of message cannot correctly be determined, then will be return an empty set.
   * Typically, this shows something went wrong
   *
   * @return message types or empty list if request not message or message type cannot be determined
   */
  @NonNull
  Set<MessageType> getMessageTypes();

  /**
   * if {@link #getRequestType()} == {@link RequestType#MESSAGE} and a field 'text' is empty, than
   * can be field 'caption' if it presents
   *
   * @return text of a message or other text of request
   * @see AbstractTelegramUpdateRequest#AbstractTelegramUpdateRequest(Update)
   */
  @Nullable
  String getText();

  @Nullable
  Message getMessage();

  @Nullable
  Chat getChat();

  @Nullable
  User getUser();

  @NonNull
  Update getOrigin();

  //-----------other getters-----------

  @NonNull
  AbsSender getAbsSender();

  /**
   * Can be null only before {@link RoleFilter} execution
   *
   * @return user permission
   */
  Permission getPermission();

  @Nullable
  Scenario getScenario();

  @Nullable
  TelegramResponse getResponse();

  @Nullable
  Throwable getError();

  @NonNull
  TelegramProperties getProperties();

  @Nullable
  RequestHandler getRequestHandler();

  @NonNull
  ObjectMapper getObjectMapper();

  //-----------setters-----------

  void setScenario(Scenario scenario);

  void setResponse(TelegramResponse response);

  void setRequestHandler(RequestHandler requestHandler);

  void setPermission(Permission permission);
}
