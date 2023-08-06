package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyHandlerResponse implements HandlerResponse {

  public static final EmptyHandlerResponse INSTANCE = new EmptyHandlerResponse();

  @Override
  public void process(TelegramUpdateRequest updateRequest) {
    // do nothing
  }
}
