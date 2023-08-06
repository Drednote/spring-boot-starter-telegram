package com.github.drednote.telegram.core.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.datasource.Permission.DefaultPermission;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import com.github.drednote.telegram.updatehandler.scenario.Scenario;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Getter
public class DefaultTelegramUpdateRequest extends AbstractTelegramUpdateRequest {

  @JsonIgnore
  private final AbsSender absSender;
  @JsonIgnore
  private final TelegramProperties properties;

  @Setter
  @NonNull
  private Permission permission;

  // mvc
  @Setter
  @Nullable
  private RequestHandler requestHandler;

  // scenario
  @Setter
  @Nullable
  private Scenario scenario;

  // response
  @Setter
  @Nullable
  @JsonIgnore
  private HandlerResponse response;
  @Setter
  @JsonIgnore
  private ObjectMapper objectMapper;

  /**
   * If error occurred during update handling
   */
  @Setter
  @Nullable
  private Throwable error;

  public DefaultTelegramUpdateRequest(
      @NonNull Update update, AbsSender absSender, TelegramProperties properties
  ) {
    super(update);
    this.absSender = absSender;
    this.properties = properties;
    this.permission = new DefaultPermission(new HashSet<>());
  }

  public DefaultTelegramUpdateRequest(DefaultTelegramUpdateRequest request) {
    super(request);
    this.properties = request.properties;
    this.absSender = request.absSender;
    this.requestHandler = request.requestHandler;
    this.scenario = request.scenario;
    this.response = request.response;
    this.objectMapper = request.objectMapper;
    this.error = request.error;
    this.permission = request.permission;
  }

  @Override
  public String toString() {
    return "Update = %s".formatted(this.origin);
  }
}
