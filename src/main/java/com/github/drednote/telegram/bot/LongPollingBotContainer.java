package com.github.drednote.telegram.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.core.ImmutableUpdateRequest;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class LongPollingBotContainer extends TelegramLongPollingBot {

  private final String name;
  private final Collection<UpdateHandler> updateHandlers;
  private final ObjectMapper objectMapper;

  public LongPollingBotContainer(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    this.name = properties.getName();
    this.updateHandlers = updateHandlers;
    this.objectMapper = objectMapper;
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
        request.setResponse(new NotHandledHandlerResponse());
      }
      processResponse(request);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void processResponse(UpdateRequest request) throws TelegramApiException, IOException {
    HandlerResponse response = request.getResponse();
    request.setObjectMapper(objectMapper);
    response.process(new ImmutableUpdateRequest(request));
  }

  @Override
  public String getBotUsername() {
    return name;
  }
}
