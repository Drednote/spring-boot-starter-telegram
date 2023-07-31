package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyHandlerResponse implements HandlerResponse {

  public static final EmptyHandlerResponse INSTANCE = new EmptyHandlerResponse();

  @Override
  public void process(UpdateRequest updateRequest) {
    // do nothing
  }
}
