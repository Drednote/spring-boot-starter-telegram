package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

/**
 * Represents a response that processes a Flux of Telegram updates.
 * <p>
 * Note: It is recommended to avoid using this class directly. Instead, if you need to execute
 * {@code Flux} of {@code TelegramResponse} instances, you can return a {@code Flux} of
 * {@code TelegramResponse} directly from your handler method.
 *
 * @author Ivan Galushko
 */
public class FluxTelegramResponse extends AbstractTelegramResponse {

    private final Flux<Object> response;
    @Nullable
    private TelegramApiException exception = null;

    /**
     * Constructs a {@code FluxTelegramResponse} with the specified response object.
     *
     * @param response the response object; must be an instance of {@code Flux}
     * @throws IllegalArgumentException if {@code response} is null or if {@code response} is not an
     *                                  instance of {@code Flux}
     */
    public FluxTelegramResponse(Object response) {
        Assert.required(response, "response");
        if (!(response instanceof Flux)) {
            throw new IllegalArgumentException("This class works only with a Flux");
        }
        this.response = (Flux<Object>) response;
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
                            new GenericTelegramResponse(o).process(request);
                            this.exception = null;
                        } catch (TelegramApiException e) {
                            if (exception == null) {
                                this.exception = e;
                            } else {
                                throw new FluxException(e);
                            }
                        }
                    }
                })
                .blockLast();
            if (exception != null) {
                throw exception;
            }
        } catch (FluxException e) {
            throw e.exception;
        }
    }

    /**
     * Exception thrown during Flux processing.
     */
    private static class FluxException extends RuntimeException {

        TelegramApiException exception;

        /**
         * Constructs a {@code FluxException} with the specified Telegram API exception.
         *
         * @param exception the Telegram API exception that caused the Flux to fail; must not be
         *                  null
         */
        public FluxException(TelegramApiException exception) {
            this.exception = exception;
        }
    }
}