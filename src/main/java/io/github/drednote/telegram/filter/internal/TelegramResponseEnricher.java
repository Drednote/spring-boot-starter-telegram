package io.github.drednote.telegram.filter.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.response.AbstractTelegramResponse;
import io.github.drednote.telegram.response.SimpleMessageTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;

/**
 * Strategy interface for enriching {@link TelegramResponse} instances with additional required fields before sending
 * them to the Telegram API.
 * <p>
 * Typically used to inject dependencies (such as {@code ParseMode}, {@link ObjectMapper}, or
 * {@link TelegramMessageSource}) into various response types like {@link AbstractTelegramResponse} or
 * {@link SimpleMessageTelegramResponse}.
 * </p>
 *
 * @author Ivan Galushko
 */
public interface TelegramResponseEnricher {

    /**
     * Enrich the given {@link TelegramResponse} with required fields such as parse mode, message source, or resolvers.
     *
     * @param telegramResponse the response to be enriched
     */
    void enrich(TelegramResponse telegramResponse);
}
