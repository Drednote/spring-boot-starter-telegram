package io.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.session.UpdateRequestContext;
import io.github.drednote.telegram.updatehandler.UpdateHandler;
import io.github.drednote.telegram.updatehandler.response.AbstractTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.TelegramResponse;
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
 * {@link DefaultTelegramUpdateRequest} is created to encapsulate the {@link Update}. The request is
 * then processed through a series of steps: pre-filtering, handling, post-filtering, and answering.
 * Any exceptions thrown during processing are handled by the exception handler
 *
 * @author Galushko Ivan
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
   * Handles the received update. Creates a {@link  DefaultTelegramUpdateRequest} to encapsulate the
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
    DefaultTelegramUpdateRequest request = new DefaultTelegramUpdateRequest(
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
  private void doReceive(DefaultTelegramUpdateRequest request) {
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
  private void doPreFilter(DefaultTelegramUpdateRequest request) {
    List<PreUpdateFilter> filters = updateFilterProvider.getPreFilters(request);
    Iterator<PreUpdateFilter> iterator = filters.iterator();
    do {
      iterator.next().preFilter(request);
    } while (request.getResponse() == null && iterator.hasNext());
  }

  /**
   * Performs post-filtering on the update request
   *
   * @param request the update request
   */
  private void doPostFilter(DefaultTelegramUpdateRequest request) {
    List<PostUpdateFilter> filters = updateFilterProvider.getPostFilters(request);
    Iterator<PostUpdateFilter> iterator = filters.iterator();
    do {
      iterator.next().postFilter(request);
    } while (iterator.hasNext());
  }

  /**
   * Handles the update request by invoking the update handlers
   *
   * @param request the update request
   * @throws Exception if an error occurs during handling
   */
  private void doHandle(DefaultTelegramUpdateRequest request) throws Exception {
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
  private void doAnswer(DefaultTelegramUpdateRequest request) throws TelegramApiException {
    TelegramResponse response = request.getResponse();
    if (response != null) {
      if (response instanceof AbstractTelegramResponse abstractHandlerResponse) {
        abstractHandlerResponse.setMessageSource(messageSource);
      }
      response.process(new DefaultTelegramUpdateRequest(request));
    }
  }

  /**
   * Handles any exceptions thrown during update processing
   *
   * @param request the update request
   * @param e       the exception thrown
   */
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
