package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.RequestType;
import com.github.drednote.telegram.updatehandler.mvc.annotation.BotController;
import com.github.drednote.telegram.updatehandler.mvc.annotation.BotRequest;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@BotController
public class TestController {

  @Getter
  private int registerCount = 0;
  @Getter
  private int textCount = 0;


  @BotRequest(value = "/register", type = RequestType.COMMAND)
  public HandlerResponse register(Update update) {
    registerCount++;
    return new EmptyHandlerResponse();
  }

  @BotRequest(type = RequestType.MESSAGE)
  public void text(
      Update update, Message message, User user
  ) {
    log.info("update -> " + update);
    log.info("message -> " + message);
    log.info("user -> " + user);
    textCount++;
  }
}
