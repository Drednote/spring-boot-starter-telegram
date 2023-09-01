package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.response.NotHandledTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.lang.NonNull;

/**
 * Implementation of a post-update filter for handling not handled Telegram update requests.
 *
 * <p>This class implements the {@link PostUpdateFilter} interface and is responsible for checking
 * if an incoming Telegram update request has not been handled. If the update request does not have
 * a response and the default answer is enabled in the properties, this filter sets the
 * {@link NotHandledTelegramResponse} as the response for the update request.
 *
 * @author Ivan Galushko
 * @see NotHandledTelegramResponse
 */
public class NotHandledUpdateFilter implements PostUpdateFilter {

  /**
   * Post-filters the incoming Telegram update request to handle not handled cases.
   *
   * <p>If the update request does not have a response and the default answer is enabled in the
   * properties, this method sets the {@link NotHandledTelegramResponse} as the response for the
   * update request.
   *
   * @param request The incoming Telegram update request to be post-filtered, not null
   */
  @Override
  public void postFilter(@NonNull TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");
    if (request.getResponse() == null
        && request.getProperties().getFilters().isSetDefaultAnswer()) {
      request.setResponse(NotHandledTelegramResponse.INSTANCE);
    }
  }
}
