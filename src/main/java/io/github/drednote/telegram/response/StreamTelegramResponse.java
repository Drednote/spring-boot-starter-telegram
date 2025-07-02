package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Represents a response that processes a stream of Telegram updates.
 * <p>
 * Note: It is recommended to avoid using this class directly. Instead, if you need to execute
 * {@code Stream} of {@code TelegramResponse} instances, you can return a {@code Stream} of
 * {@code TelegramResponse} directly from your handler method.
 *
 * @author Ivan Galushko
 */
public class StreamTelegramResponse extends AbstractTelegramResponse {

    private final Stream<?> response;
    @Nullable
    private TelegramApiException exception = null;

    /**
     * Constructs a {@code StreamTelegramResponse} with the specified response stream.
     *
     * @param response the stream of updates to be processed; must not be null
     * @throws IllegalArgumentException if {@code response} is null
     */
    public StreamTelegramResponse(Stream<?> response) {
        Assert.required(response, "response");
        this.response = response;
    }

    /**
     * Processes the specified update request using the stream of updates.
     *
     * @param request the update request to process; must not be null
     * @throws TelegramApiException if a processing error occurs
     */
    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        try {
            response.forEach(o -> {
                if (o != null) {
                    try {
                        TelegramResponseHelper.create(wrapWithTelegramResponse(o))
                            .propagateProperties(this)
                            .process(request);

                        this.exception = null;
                    } catch (TelegramApiException e) {
                        if (exception == null) {
                            this.exception = e;
                        } else {
                            throw new StreamException(e);
                        }
                    }
                }
            });
            if (exception != null) {
                throw exception;
            }
        } catch (StreamException e) {
            throw e.exception;
        }
    }

    /**
     * Exception thrown during stream processing.
     */
    private static class StreamException extends RuntimeException {

        TelegramApiException exception;

        /**
         * Constructs a {@code StreamException} with the specified Telegram API exception.
         *
         * @param exception the Telegram API exception that caused the stream to fail; must not be
         *                  null
         */
        public StreamException(TelegramApiException exception) {
            this.exception = exception;
        }
    }
}
