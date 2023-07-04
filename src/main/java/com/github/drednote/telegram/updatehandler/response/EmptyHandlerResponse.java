package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EmptyHandlerResponse implements HandlerResponse {

  @Override
  public void process(UpdateRequest updateRequest) {
    // do nothing
  }
}
