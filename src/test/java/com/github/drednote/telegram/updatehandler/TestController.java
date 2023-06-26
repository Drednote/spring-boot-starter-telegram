package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.BotController;
import com.github.drednote.telegram.updatehandler.mvc.BotRequest;
import com.github.drednote.telegram.updatehandler.mvc.RequestUpdate;
import com.github.drednote.telegram.updatehandler.response.EmptyUpdateHandlerResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@BotController
public class TestController {

  @Getter
  private int registerCount = 0;
  @Getter
  private int textCount = 0;


  @BotRequest("/register")
  public UpdateHandlerResponse register(@RequestUpdate Update update) {
    registerCount++;
    return new EmptyUpdateHandlerResponse(update);
  }

  @BotRequest("**")
  public void text(@RequestUpdate Update update) {
    textCount++;
  }
}
