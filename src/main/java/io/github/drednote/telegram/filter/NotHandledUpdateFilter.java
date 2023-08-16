package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.response.NotHandledTelegramResponse;
import org.springframework.lang.NonNull;

public class NotHandledUpdateFilter implements PostUpdateFilter {

  @Override
  public void postFilter(@NonNull TelegramUpdateRequest request) {
    if (request.getResponse() == null
        && request.getProperties().getFilters().isSetDefaultAnswer()) {
      request.setResponse(NotHandledTelegramResponse.INSTANCE);
    }
  }
}
