package com.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.exception.ExceptionHandler;
import com.github.drednote.telegram.filter.UpdateFilter;
import com.github.drednote.telegram.filter.UpdateFilterProvider;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LongPollingBot extends TelegramLongPollingBot {

  private final String name;
  private final Collection<UpdateHandler> updateHandlers;
  private final ObjectMapper objectMapper;
  private final ExceptionHandler exceptionHandler;
  private final TelegramProperties telegramProperties;
  private final UpdateFilterProvider updateFilterProvider;

  public LongPollingBot(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
      UpdateFilterProvider updateFilterProvider
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());

    this.name = properties.getName();
    this.updateHandlers = updateHandlers.stream()
        .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
    this.objectMapper = objectMapper;
    this.exceptionHandler = exceptionHandler;
    this.telegramProperties = properties;
    this.updateFilterProvider = updateFilterProvider;
  }

  @Override
  public void onUpdateReceived(Update update) {
    UpdateRequest request = new UpdateRequest(update, this, telegramProperties);
    try {
      doFilter(request);
      if (request.getResponse() == null) {
        doHandle(request);
      }
      doAnswer(request);
    } catch (TelegramApiException e) {
      handleException(request, e);
    } catch (Exception e) {
      handleException(request, e);
      try {
        doAnswer(request);
      } catch (Exception ex) {
        handleException(request, ex);
      }
    }
  }

  private void doFilter(UpdateRequest request) throws Exception {
    Collection<UpdateFilter> filters = updateFilterProvider.resolve(request);
    Iterator<UpdateFilter> iterator = filters.iterator();
    do {
      iterator.next().filter(request);
    } while (request.getResponse() == null && iterator.hasNext());
  }

  private void doHandle(UpdateRequest request) throws Exception {
    for (UpdateHandler updateHandler : updateHandlers) {
      if (request.getResponse() == null) {
        updateHandler.onUpdate(request);
      }
    }
    if (request.getResponse() == null
        && request.getProperties().getUpdateHandler().isSetDefaultAnswer()) {
      request.setResponse(new NotHandledHandlerResponse());
    }
  }

  private void doAnswer(UpdateRequest request) throws TelegramApiException {
    HandlerResponse response = request.getResponse();
    if (response != null) {
      request.setObjectMapper(objectMapper);
      response.process(new ImmutableUpdateRequest(request));
    }
  }

  private void handleException(UpdateRequest request, Exception e) {
    request.setError(e);
    if (!(e instanceof TelegramApiException)) {
      request.setResponse(null);
    }
    exceptionHandler.handle(request);
  }

  @Override
  public String getBotUsername() {
    return name;
  }
}
