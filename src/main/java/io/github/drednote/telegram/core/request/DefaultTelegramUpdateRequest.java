package io.github.drednote.telegram.core.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.updatehandler.response.TelegramResponse;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.scenario.Scenario;
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

  private Permission permission;

  // mvc
  @Nullable
  private RequestHandler requestHandler;

  // scenario
  @Nullable
  private Scenario scenario;

  // response
  @Setter
  @Nullable
  @JsonIgnore
  private TelegramResponse response;
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
  }

  public DefaultTelegramUpdateRequest(TelegramUpdateRequest request) {
    super(request);
    this.properties = request.getProperties();
    this.absSender = request.getAbsSender();
    this.requestHandler = request.getRequestHandler();
    this.scenario = request.getScenario();
    this.response = request.getResponse();
    this.objectMapper = request.getObjectMapper();
    this.error = request.getError();
    this.permission = request.getPermission();
  }

  @Override
  public String toString() {
    return "Id = %s, text = %s".formatted(this.id, this.text);
  }

  @Override
  public void setScenario(Scenario scenario) {
    if (this.scenario == null) {
      this.scenario = scenario;
    }
  }

  @Override
  public void setRequestHandler(RequestHandler requestHandler) {
    if (this.requestHandler == null) {
      this.requestHandler = requestHandler;
    }
  }

  @Override
  public void setPermission(Permission permission) {
    if (this.permission == null) {
      this.permission = permission;
    }
  }
}
