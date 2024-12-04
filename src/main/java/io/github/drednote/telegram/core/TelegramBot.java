package io.github.drednote.telegram.core;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This interface represents a Telegram Bot that can receive updates from the Telegram API.
 *
 * @author Ivan Galushko
 */
public interface TelegramBot {

    /**
     * Called when an update is received from the Telegram API.
     *
     * @param update the {@code Update} object containing the details of the received update
     */
    void onUpdateReceived(Update update);
}
