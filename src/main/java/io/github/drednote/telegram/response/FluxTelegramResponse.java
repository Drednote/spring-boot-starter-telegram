package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a response that processes a Flux of Telegram updates.
 * <p>
 * Note: It is recommended to avoid using this class directly. Instead, if you need to execute {@code Flux} of
 * {@code TelegramResponse} instances, you can return a {@code Flux} of {@code TelegramResponse} directly from your
 * handler method.
 *
 * @author Ivan Galushko
 */
public class FluxTelegramResponse extends AbstractTelegramResponse {

    private final Flux<?> response;

    /**
     * Constructs a {@code FluxTelegramResponse} with the specified response object.
     *
     * @param response the response object
     * @throws IllegalArgumentException if {@code response} is null
     */
    public FluxTelegramResponse(Flux<?> response) {
        Assert.required(response, "response");

        this.response = response;
    }

    /**
     * Processes the specified update request using the Flux of updates.
     *
     * @param request the update request to process; must not be null
     * @throws TelegramApiException if a processing error occurs
     */
    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        try {
            response.doOnNext(o -> {
                    if (o != null) {
                        try {
                            TelegramResponseHelper.create(wrapWithTelegramResponse(o))
                                .propagateProperties(this)
                                .process(request);
                        } catch (TelegramApiException e) {
                            throw new FluxException(e);
                        }
                    }
                })
                .blockLast();
        } catch (FluxException e) {
            throw e.exception;
        }
    }

    @Override
    public Mono<Void> processReactive(UpdateRequest request) {
        return response.flatMap(o -> {
            if (o == null) {
                return Mono.empty();
            }

            return TelegramResponseHelper.create(wrapWithTelegramResponse(o))
                .propagateProperties(this)
                .processReactive(request);
        }).then();
    }

    /**
     * Exception thrown during Flux processing.
     */
    private static class FluxException extends RuntimeException {

        TelegramApiException exception;

        /**
         * Constructs a {@code FluxException} with the specified Telegram API exception.
         *
         * @param exception the Telegram API exception that caused the Flux to fail; must not be null
         */
        public FluxException(TelegramApiException exception) {
            this.exception = exception;
        }
    }
}