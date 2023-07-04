package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmptyHandlerResponse implements HandlerResponse {

  @Override
  public void process(UpdateRequest updateRequest) {
    // do nothing
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
