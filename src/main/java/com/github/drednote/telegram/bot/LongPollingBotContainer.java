package com.github.drednote.telegram.bot;

import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class LongPollingBotContainer extends TelegramLongPollingBot {

  private final String name;
  private final UpdateHandler updateHandler;

  public LongPollingBotContainer(TelegramProperties properties, UpdateHandler updateHandler) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    this.name = properties.getName();
    this.updateHandler = updateHandler;
  }

  @Override
  public void onUpdateReceived(Update update) {
    try {
      UpdateRequest request = new UpdateRequest(update, this);
      updateHandler.onUpdate(request);
      request.getResponse().process(this);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public String getBotUsername() {
    return name;
  }
}
