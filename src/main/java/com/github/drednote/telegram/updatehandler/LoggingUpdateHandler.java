package com.github.drednote.telegram.updatehandler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class LoggingUpdateHandler implements UpdateHandler {

  @Override
  public UpdateHandlerResponse onUpdate(Update update) {
    log.info("Received update {}", update);
    return null;
  }
}
