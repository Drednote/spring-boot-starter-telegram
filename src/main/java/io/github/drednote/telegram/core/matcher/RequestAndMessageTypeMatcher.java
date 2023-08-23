package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.Ordered;

/**
 * The {@code RequestTypeMatcher} class is an implementation of the {@code RequestMatcher} interface
 * that matches requests based on the request type and message types. It checks if the request type
 * of the given update request matches the request type specified in the mapping, and if it is true,
 * checks the message types
 *
 * @author Galushko Ivan
 * @see TelegramRequestMapping
 */
public class RequestAndMessageTypeMatcher implements RequestMatcher {

  /**
   * The Telegram request mapping
   */
  private final TelegramRequestMapping mapping;

  /**
   * @param mapping the Telegram request mapping, not null
   */
  public RequestAndMessageTypeMatcher(TelegramRequestMapping mapping) {
    Assert.required(mapping, "TelegramRequestMapping");

    this.mapping = mapping;
  }

  /**
   * Checks if the given update request matches the request type specified in the mapping. If
   * matches than check message types
   * <p>
   * If a request type doesn't present in current {@code TelegramRequestMapping}, then return true
   * <p>
   * If the given update request matches the request type specified in the mapping and message types
   * are empty, then return true
   *
   * @param request the update request to match
   * @return true if the request matches criteria, false otherwise
   */
  @Override
  public boolean matches(TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");

    RequestType requestType = request.getRequestType();
    if (mapping.getRequestType() != null && requestType != mapping.getRequestType()) {
      return false;
    }
    return mapping.getMessageTypes().isEmpty()
        || request.getMessageTypes().containsAll(mapping.getMessageTypes());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
