package com.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import com.github.drednote.telegram.exception.ExceptionHandler;
import com.github.drednote.telegram.filter.UpdateFilter;
import com.github.drednote.telegram.filter.UpdateFilterProvider;
import com.github.drednote.telegram.session.BotSessionContext;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.AbstractHandlerResponse;
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
  private final BotMessageSource messageSource;

  public LongPollingBot(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
      UpdateFilterProvider updateFilterProvider, BotMessageSource messageSource
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());

    this.name = properties.getName();
    this.updateHandlers = updateHandlers.stream()
        .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
    this.objectMapper = objectMapper;
    this.exceptionHandler = exceptionHandler;
    this.telegramProperties = properties;
    this.updateFilterProvider = updateFilterProvider;
    this.messageSource = messageSource;
  }

  @Override
  public void onUpdateReceived(Update update) {
    DefaultTelegramUpdateRequest request = new DefaultTelegramUpdateRequest(update, this, telegramProperties);
    request.setObjectMapper(objectMapper);
    try {
      BotSessionContext.saveRequest(request);
      doReceive(request);
    } catch (Exception ex) {
      handleException(request, ex);
    } finally {
      BotSessionContext.removeRequest(true);
    }
  }

  private void doReceive(DefaultTelegramUpdateRequest request) {
    try {
      doPreFilter(request);
      doHandle(request);
    } catch (Exception e) {
      handleException(request, e);
    }
    try {
      doAnswer(request);
    } catch (Exception e) {
      handleException(request, e);
    } finally {
      doPostFilter(request);
    }
  }

  private void doPreFilter(DefaultTelegramUpdateRequest request) {
    Collection<UpdateFilter> filters = updateFilterProvider.getPreFilters(request);
    Iterator<UpdateFilter> iterator = filters.iterator();
    do {
      iterator.next().preFilter(request);
    } while (request.getResponse() == null && iterator.hasNext());
  }

  private void doPostFilter(DefaultTelegramUpdateRequest request) {
    Collection<UpdateFilter> filters = updateFilterProvider.getPostFilters(request);
    Iterator<UpdateFilter> iterator = filters.iterator();
    do {
      iterator.next().postFilter(request);
    } while (iterator.hasNext());
  }

  private void doHandle(DefaultTelegramUpdateRequest request) throws Exception {
    for (UpdateHandler updateHandler : updateHandlers) {
      if (request.getResponse() == null) {
        updateHandler.onUpdate(request);
      }
    }
    if (request.getResponse() == null
        && request.getProperties().getUpdateHandler().isSetDefaultAnswer()) {
      request.setResponse(NotHandledHandlerResponse.INSTANCE);
    }
  }

  private void doAnswer(DefaultTelegramUpdateRequest request) throws TelegramApiException {
    HandlerResponse response = request.getResponse();
    if (response != null) {
      if (response instanceof AbstractHandlerResponse abstractHandlerResponse) {
        abstractHandlerResponse.setMessageSource(messageSource);
      }
      response.process(new DefaultTelegramUpdateRequest(request));
    }
  }

  private void handleException(DefaultTelegramUpdateRequest request, Exception e) {
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
