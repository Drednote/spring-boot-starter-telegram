package com.github.drednote.telegram.bot;

import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class LongPollingBotContainer extends TelegramLongPollingBot {

  private final String name;
  private final Collection<UpdateHandler> updateHandlers;

  public LongPollingBotContainer(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    this.name = properties.getName();
    this.updateHandlers = updateHandlers;
  }

  @Override
  public void onUpdateReceived(Update update) {
    try {
      UpdateRequest request = new UpdateRequest(update, this);
      for (UpdateHandler updateHandler : updateHandlers) {
        if (request.getResponse() == null) {
          updateHandler.onUpdate(request);
        }
      }
      if (request.getResponse() == null) {
        request.setResponse(new NotHandledHandlerResponse(update));
      }
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
