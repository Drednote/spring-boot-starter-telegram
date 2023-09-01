package io.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.response.SimpleMessageTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * The {@code LongPollingBot} class extends the {@code TelegramLongPollingBot} class and serves as
 * the main bot implementation for handling updates. The bot overrides the `onUpdateReceived()`
 * method to handle incoming updates. Within the {@link #onUpdateReceived} method, a
 * {@link DefaultUpdateRequest} is created to encapsulate the {@link Update}. The request is
 * then processed through a series of steps: pre-filtering, handling, post-filtering, and answering.
 * Any exceptions thrown during processing are handled by the exception handler
 *
 * @author Ivan Galushko
 * @see UpdateHandler
 * @see ExceptionHandler
 * @see UpdateFilterProvider
 */
public class LongPollingBot extends TelegramLongPollingBot {

  /**
   * The name of the bot
   */
  private final String name;
  /**
   * The collection of update handlers for processing updates
   */
  private final Collection<UpdateHandler> updateHandlers;
  /**
   * The object mapper for serializing and deserializing JSON
   */
  private final ObjectMapper objectMapper;
  /**
   * The exception handler for handling exceptions during update processing
   */
  private final ExceptionHandler exceptionHandler;
  /**
   * The Telegram properties
   */
  private final TelegramProperties telegramProperties;
  /**
   * The update filter provider for managing pre-update handlers and post-update handlers filters
   */
  private final UpdateFilterProvider updateFilterProvider;
  /**
   * The message source for retrieving localized messages
   */
  private final TelegramMessageSource messageSource;

  /**
   * Creates a new instance of the {@code LongPollingBot} class with the provided properties and
   * dependencies
   *
   * @param properties           the Telegram properties, not null
   * @param updateHandlers       the collection of update handlers, not null
   * @param objectMapper         the object mapper, not null
   * @param exceptionHandler     the exception handler, not null
   * @param updateFilterProvider the update filter provider, not null
   * @param messageSource        the message source, not null
   */
  public LongPollingBot(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
      UpdateFilterProvider updateFilterProvider, TelegramMessageSource messageSource
  ) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    Assert.required(updateHandlers, "Collection of UpdateHandlers");
    Assert.required(objectMapper, "ObjectMapper");
    Assert.required(exceptionHandler, "ExceptionHandler");
    Assert.required(updateFilterProvider, "UpdateFilterProvider");
    Assert.required(messageSource, "TelegramMessageSource");

    this.name = properties.getName();
    this.updateHandlers = updateHandlers.stream()
        .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
    this.objectMapper = objectMapper;
    this.exceptionHandler = exceptionHandler;
    this.telegramProperties = properties;
    this.updateFilterProvider = updateFilterProvider;
    this.messageSource = messageSource;
  }

  /**
   * Handles the received update. Creates a {@link  DefaultUpdateRequest} to encapsulate the
   * {@link Update}. Processes the request through pre-filtering, handling, post-filtering, and
   * answering. Handle any exceptions thrown during processing.
   * <p>
   * Before processing saves request to context, for further usage. After processing delete request
   * from context and spring beans too if any were created
   *
   * @param update the received update, not null
   */
  @Override
  public void onUpdateReceived(Update update) {
    DefaultUpdateRequest request = new DefaultUpdateRequest(
        update, this, telegramProperties, objectMapper);
    try {
      UpdateRequestContext.saveRequest(request);
      doReceive(request);
    } finally {
      UpdateRequestContext.removeRequest(true);
    }
  }

  /**
   * Performs the processing of the received update. Executes pre-filtering, handling,
   * post-filtering, and answering. Handle any exceptions thrown during processing
   *
   * @param request the update request
   */
  private void doReceive(DefaultUpdateRequest request) {
    try {
      doPreFilter(request);
      doHandle(request);
    } catch (Exception e) {
      handleException(request, e);
    } finally {
      try {
        doPostFilter(request);
        doAnswer(request);
      } catch (Exception e) {
        handleException(request, e);
      }
    }
  }

  /**
   * Performs pre-filtering on the update request
   *
   * @param request the update request
   */
  private void doPreFilter(DefaultUpdateRequest request) {
    List<PreUpdateFilter> filters = updateFilterProvider.getPreFilters(request);
    Iterator<PreUpdateFilter> iterator = filters.iterator();
    while (request.getResponse() == null && iterator.hasNext()) {
      iterator.next().preFilter(request);
    }
  }

  /**
   * Performs post-filtering on the update request
   *
   * @param request the update request
   */
  private void doPostFilter(DefaultUpdateRequest request) {
    List<PostUpdateFilter> filters = updateFilterProvider.getPostFilters(request);
    for (PostUpdateFilter filter : filters) {
      filter.postFilter(request);
    }
  }

  /**
   * Handles the update request by invoking the update handlers
   *
   * @param request the update request
   * @throws Exception if an error occurs during handling
   */
  private void doHandle(DefaultUpdateRequest request) throws Exception {
    for (UpdateHandler updateHandler : updateHandlers) {
      if (request.getResponse() == null) {
        updateHandler.onUpdate(request);
      }
    }
  }

  /**
   * Answers the update request by processing the response
   *
   * @param request the update request
   * @throws TelegramApiException if an error occurs during processing answer
   */
  private void doAnswer(DefaultUpdateRequest request) throws TelegramApiException {
    TelegramResponse response = request.getResponse();
    if (response != null) {
      if (response instanceof SimpleMessageTelegramResponse simpleMessageTelegramResponse) {
        simpleMessageTelegramResponse.setMessageSource(messageSource);
      }
      response.process(new DefaultUpdateRequest(request));
    }
  }

  /**
   * Handles any exceptions thrown during update processing
   *
   * @param request the update request
   * @param e       the exception thrown
   */
  private void handleException(DefaultUpdateRequest request, Exception e) {
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
