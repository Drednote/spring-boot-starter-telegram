package io.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.response.AbstractTelegramResponse;
import io.github.drednote.telegram.response.SimpleMessageTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.response.resolver.CompositeTelegramResponseTypesResolver;
import io.github.drednote.telegram.response.resolver.TelegramResponseTypesResolver;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The {@code DefaultTelegramBot} class extends the {@code TelegramBot} class and serves as the main bot implementation
 * for handling updates. The bot overrides the `onUpdateReceived()` method to handle incoming updates. Within the
 * {@link #onUpdateReceived} method, a {@link DefaultUpdateRequest} is created to encapsulate the {@link Update}. The
 * request is then processed through a series of steps: pre-filtering, handling, post-filtering, and answering. Any
 * exceptions thrown during processing are handled by the exception handler
 *
 * @author Ivan Galushko
 * @see UpdateHandler
 * @see ExceptionHandler
 * @see UpdateFilterProvider
 */
public class ReactiveTelegramBot implements TelegramBot {

    private static final Logger log = LoggerFactory.getLogger(ReactiveTelegramBot.class);

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
    private final TelegramClient telegramClient;
    private final TelegramResponseTypesResolver resolver;

    /**
     * Creates a new instance of the {@code DefaultTelegramBot} class with the provided properties and dependencies
     *
     * @param properties           the Telegram properties, not null
     * @param updateHandlers       the collection of update handlers, not null
     * @param objectMapper         the object mapper, not null
     * @param exceptionHandler     the exception handler, not null
     * @param updateFilterProvider the update filter provider, not null
     * @param messageSource        the message source, not null
     */
    public ReactiveTelegramBot(
        TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
        ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
        UpdateFilterProvider updateFilterProvider, TelegramMessageSource messageSource,
        TelegramClient telegramClient, Collection<TelegramResponseTypesResolver> resolvers
    ) {
        Assert.required(updateHandlers, "Collection of UpdateHandlers");
        Assert.required(objectMapper, "ObjectMapper");
        Assert.required(exceptionHandler, "ExceptionHandler");
        Assert.required(updateFilterProvider, "UpdateFilterProvider");
        Assert.required(messageSource, "TelegramMessageSource");
        Assert.required(telegramClient, "TelegramClient");

        this.resolver = new CompositeTelegramResponseTypesResolver(resolvers);
        this.telegramClient = telegramClient;
        this.updateHandlers = updateHandlers.stream()
            .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
        this.telegramProperties = properties;
        this.updateFilterProvider = updateFilterProvider;
        this.messageSource = messageSource;
    }

    /**
     * Handles the received update. Creates a {@link  DefaultUpdateRequest} to encapsulate the {@link Update}. Processes
     * the request through pre-filtering, handling, post-filtering, and answering. Handle any exceptions thrown during
     * processing.
     * <p>
     * Before processing saves request to context, for further usage. After processing delete request from context and
     * spring beans too if any were created
     *
     * @param update the received update, not null
     */
    @Override
    public void onUpdateReceived(Update update) {
        DefaultUpdateRequest request = new DefaultUpdateRequest(
            update, telegramClient, telegramProperties, objectMapper);
        try {
            UpdateRequestContext.saveRequest(request);
            doReceive(request);
        } finally {
            UpdateRequestContext.removeRequest(true);
        }
    }

    /**
     * Performs the processing of the received update. Executes pre-filtering, handling, post-filtering, and answering.
     * Handle any exceptions thrown during processing
     *
     * @param request the update request
     */
    private void doReceive(DefaultUpdateRequest request) {
        Mono.empty()
            .then(Mono.defer(() -> doPreFilter(request)))
            .then(Mono.defer(() -> doHandle(request)))
            .onErrorResume(e -> handleException(request, e))
            .then(Mono.defer(() -> doPostFilter(request)))
            .then(Mono.defer(() -> doAnswer(request)))
            .onErrorResume(e -> Mono.defer(() -> handleException(request, e)).then(Mono.defer(() -> {
                if (request.getResponse() != null) {
                    return doAnswer(request).onErrorResume(ex -> handleException(request, ex));
                }
                return Mono.empty();
            })))
            .then(Mono.defer(() -> doConclusivePostFilter(request)))
            .onErrorResume(e -> handleException(request, e))
            .block();
    }

    /**
     * Performs pre-filtering on the update request
     *
     * @param request the update request
     */
    private Mono<Void> doPreFilter(DefaultUpdateRequest request) {
        return Flux.fromIterable(updateFilterProvider.getPreFilters(request))
            .takeWhile(filter -> request.getResponse() == null)
            .filter(filter -> filter.matches(request))
            .concatMap(filter -> {
                log.trace("Executing pre filter -> {}", filter);
                return filter.preFilterReactive(request);
            })
            .then();
    }

    /**
     * Performs post-filtering on the update request
     *
     * @param request the update request
     */
    private Mono<Void> doPostFilter(DefaultUpdateRequest request) {
        return Flux.fromIterable(updateFilterProvider.getPostFilters(request))
            .filter(filter -> filter.matches(request))
            .concatMap(filter -> {
                log.trace("Executing post filter -> {}", filter);
                return filter.postFilterReactive(request);
            })
            .then();
    }

    /**
     * Performs conclusive post-filtering on the update request
     *
     * @param request the update request
     */
    private Mono<Void> doConclusivePostFilter(DefaultUpdateRequest request) {
        return Flux.fromIterable(updateFilterProvider.getConclusivePostFilters(request))
            .filter(filter -> filter.matches(request))
            .concatMap(filter -> {
                log.trace("Executing conclusive post filter -> {}", filter);
                return filter.postFilterReactive(request);
            })
            .then();
    }

    /**
     * Handles the update request by invoking the update handlers. Propagate Exception if an error occurs during
     * handling
     *
     * @param request the update request
     */
    private Mono<Void> doHandle(DefaultUpdateRequest request) {
        return Flux.fromIterable(updateHandlers)
            .takeWhile(h -> request.getResponse() == null)
            .concatMap(handler -> handler.onUpdateReactive(request))
            .then();
    }

    /**
     * Answers the update request by processing the response. Propagate TelegramApiException if an error occurs during
     * processing answer
     *
     * @param request the update request
     */
    private Mono<Void> doAnswer(DefaultUpdateRequest request) {
        TelegramResponse response = request.getResponse();
        if (response != null) {
            if (response instanceof SimpleMessageTelegramResponse simpleMessageTelegramResponse) {
                simpleMessageTelegramResponse.setMessageSource(messageSource);
            }
            if (response instanceof AbstractTelegramResponse abstractTelegramResponse) {
                if (abstractTelegramResponse.getParseMode() == null) {
                    abstractTelegramResponse.setParseMode(telegramProperties.getUpdateHandler().getParseMode());
                }
                abstractTelegramResponse.setResolver(resolver);
            }
            return response.processReactive(request);
        } else {
            return Mono.empty();
        }
    }

    /**
     * Handles any exceptions thrown during update processing
     *
     * @param request the update request
     * @param e       the exception thrown
     */
    private Mono<Void> handleException(DefaultUpdateRequest request, Throwable e) {
        request.setError(e);
        if (!(e instanceof TelegramApiException)) {
            request.setResponse(null);
        }
        return exceptionHandler.handleReactive(request);
    }
}
