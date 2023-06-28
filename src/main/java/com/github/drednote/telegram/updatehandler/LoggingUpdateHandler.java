package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUpdateHandler implements UpdateHandler {

  @Override
  public void onUpdate(UpdateRequest descriptor) {
    log.info("Received update {}", descriptor.getOrigin());
  }
}
