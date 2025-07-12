package io.github.drednote.telegram.filter.internal;

import io.github.drednote.telegram.response.TelegramResponse;

/**
 * Enrich {@code TelegramResponse} fields with values.
 */
public interface TelegramResponseEnricher {

    /**
     * Do enrich.
     *
     * @param telegramResponse response to Telegram
     */
    void enrich(TelegramResponse telegramResponse);
}
