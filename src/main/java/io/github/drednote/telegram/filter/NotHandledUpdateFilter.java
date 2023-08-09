package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.response.NotHandledTelegramResponse;
import org.springframework.lang.NonNull;

public class NotHandledUpdateFilter implements UpdateFilter {

  @Override
  public void postFilter(@NonNull TelegramUpdateRequest request) {
    if (request.getResponse() == null
        && request.getProperties().getUpdateHandler().isSetDefaultAnswer()) {
      request.setResponse(NotHandledTelegramResponse.INSTANCE);
    }
  }
}
