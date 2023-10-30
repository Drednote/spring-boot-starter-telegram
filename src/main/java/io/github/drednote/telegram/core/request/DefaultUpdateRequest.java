package io.github.drednote.telegram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.utils.Assert;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * The {@code DefaultUpdateRequest} class is default implementation of the
 * {@link UpdateRequest} interface
 *
 * @author Ivan Galushko
 */
@Getter
public class DefaultUpdateRequest extends AbstractUpdateRequest {

  private final AbsSender absSender;
  private final TelegramProperties properties;
  private final ObjectMapper objectMapper;

  @Nullable
  private Permission permission;

  @Nullable
  private RequestHandler requestHandler;

  @Nullable
  private Scenario scenario;

  @Setter
  @Nullable
  private TelegramResponse response;

  /**
   * If error occurred during update handling
   */
  @Setter
  @Nullable
  private Throwable error;

  /**
   * Creates a new instance of the DefaultUpdateRequest class with the given parameters.
   *
   * @param update     the update received from Telegram.
   * @param absSender  the abstract sender used to send responses.
   * @param properties the Telegram properties.
   */
  public DefaultUpdateRequest(
      Update update, AbsSender absSender, TelegramProperties properties, ObjectMapper objectMapper
  ) {
    super(update);
    Assert.required(absSender, "AbsSender");
    Assert.required(properties, "TelegramProperties");
    Assert.required(objectMapper, "ObjectMapper");

    this.absSender = absSender;
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  /**
   * Create new instance of {@code DefaultUpdateRequest} class based on an existing
   * {@code UpdateRequest}
   *
   * @param request existing {@code UpdateRequest}
   */
  public DefaultUpdateRequest(UpdateRequest request) {
    super(request);
    Assert.required(request, "UpdateRequest");

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
  public void setScenario(@Nullable Scenario scenario) {
    if (this.scenario == null) {
      this.scenario = scenario;
    }
  }

  @Override
  public void setRequestHandler(@Nullable RequestHandler requestHandler) {
    if (this.requestHandler == null) {
      this.requestHandler = requestHandler;
    }
  }

  @Override
  public void setPermission(@Nullable Permission permission) {
    if (this.permission == null) {
      this.permission = permission;
    }
  }
}
