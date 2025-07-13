package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.internal.TelegramResponseEnricher;
import io.github.drednote.telegram.utils.Assert;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

/**
 * Helper class to handle and enrich a {@link TelegramResponse} before sending it to Telegram.
 * <p>
 * This class wraps a {@link TelegramResponse} instance and provides utility methods to propagate configuration
 * properties and invoke processing (both sync and reactive).
 * <p>
 * It is typically used to propagate properties from parent to child {@code TelegramResponse}.
 *
 * @author Ivan Galushko
 * @implNote If you update the propagation logic, consider also updating {@link TelegramResponseEnricher}
 */
public class TelegramResponseHelper {

    private final TelegramResponse telegramResponse;

    /**
     * Constructs a {@code TelegramResponseHelper} with the given {@code TelegramResponse}.
     *
     * @param telegramResponse the response to be wrapped and handled, must not be {@code null}
     */
    public TelegramResponseHelper(TelegramResponse telegramResponse) {
        Assert.required(telegramResponse, "TelegramResponse");

        this.telegramResponse = telegramResponse;
    }

    /**
     * Creates a new {@link TelegramResponseHelper} instance from the given {@link TelegramResponse}.
     *
     * @param telegramResponse the response to be wrapped
     * @return a new instance of {@code TelegramResponseHelper}
     */
    public static TelegramResponseHelper create(TelegramResponse telegramResponse) {
        return new TelegramResponseHelper(telegramResponse);
    }

    /**
     * Propagates relevant properties from the source {@link AbstractTelegramResponse} to the wrapped
     * {@link TelegramResponse}, if it is also an {@code AbstractTelegramResponse}.
     * <p>
     * Properties such as parse mode, response type resolver, object mapper, and serialization flag are propagated only
     * if they are not already set in the target.
     *
     * @param from the source response whose properties will be copied
     * @return this helper instance for fluent API usage
     */
    public TelegramResponseHelper propagateProperties(AbstractTelegramResponse from) {
        if (telegramResponse instanceof AbstractTelegramResponse abstractTelegramResponse) {
            if (from.getParseMode() != null && abstractTelegramResponse.getParseMode() == null) {
                abstractTelegramResponse.setParseMode(from.getParseMode());
            }
            if (from.getResolver() != null && abstractTelegramResponse.getResolver() == null) {
                abstractTelegramResponse.setResolver(from.getResolver());
            }
            if (from.getSerializeJavaObjectWithJackson() != null
                && abstractTelegramResponse.getSerializeJavaObjectWithJackson() == null) {
                abstractTelegramResponse.setSerializeJavaObjectWithJackson(
                    from.getSerializeJavaObjectWithJackson());
            }
            if (from.getObjectMapper() != null && abstractTelegramResponse.getObjectMapper() == null) {
                abstractTelegramResponse.setObjectMapper(from.getObjectMapper());
            }
        }
        return this;
    }

    /**
     * Processes the wrapped {@link TelegramResponse} synchronously.
     *
     * @param request the update request
     * @throws TelegramApiException if an error occurs while sending the response
     */
    public void process(UpdateRequest request) throws TelegramApiException {
        telegramResponse.process(request);
    }

    /**
     * Processes the wrapped {@link TelegramResponse} reactively (non-blocking).
     *
     * @param request the update request
     * @return a {@link Mono} that completes when the response is sent
     */
    public Mono<Void> processReactive(UpdateRequest request) {
        return telegramResponse.processReactive(request);
    }
}
