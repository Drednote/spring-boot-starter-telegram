package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyTelegramResponse implements TelegramResponse {

  public static final EmptyTelegramResponse INSTANCE = new EmptyTelegramResponse();

  @Override
  public void process(TelegramUpdateRequest updateRequest) {
    // do nothing
  }
}
