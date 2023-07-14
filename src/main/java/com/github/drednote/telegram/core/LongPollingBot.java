package com.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.exception.ExceptionHandler;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class LongPollingBot extends TelegramLongPollingBot {

  private final String name;
  private final Collection<UpdateHandler> updateHandlers;
  private final ObjectMapper objectMapper;
  private final ExceptionHandler exceptionHandler;
  private final TelegramProperties telegramProperties;

  public LongPollingBot(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper, ExceptionHandler exceptionHandler
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    this.name = properties.getName();
    this.updateHandlers = updateHandlers;
    this.objectMapper = objectMapper;
    this.exceptionHandler = exceptionHandler;
    this.telegramProperties = properties;
  }

  @Override
  public void onUpdateReceived(Update update) {
    UpdateRequest request = new UpdateRequest(update, this, telegramProperties);
    try {
      for (UpdateHandler updateHandler : updateHandlers) {
        if (request.getResponse() == null) {
          updateHandler.onUpdate(request);
        }
      }
      if (request.getResponse() == null
          && request.getProperties().getUpdateHandler().isSetDefaultAnswer()) {
        request.setResponse(new NotHandledHandlerResponse());
      }
      processResponse(request);
    } catch (Exception e) {
      try {
        request.setError(e);
        request.setResponse(null);
        exceptionHandler.handle(request);
        processResponse(request);
      } catch (Exception ex) {  // todo add resolver
        log.error("Internal error", e);
      }
    }
  }

  private void processResponse(UpdateRequest request) throws TelegramApiException {
    HandlerResponse response = request.getResponse();
    if (response != null) {
      request.setObjectMapper(objectMapper);
      response.process(new ImmutableUpdateRequest(request));
    }
  }

  @Override
  public String getBotUsername() {
    return name;
  }
}
