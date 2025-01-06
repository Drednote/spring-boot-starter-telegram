package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.UpdateHandlerProperties.ParseMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Abstract base class for implementing custom Telegram response actions.
 *
 * @author Ivan Galushko
 */
public abstract class AbstractTelegramResponse implements TelegramResponse {

    @Getter
    @Setter
    @Nullable
    protected ParseMode parseMode = null;

    /**
     * Sends a text message to the specified chat using the provided string
     *
     * @param string  The text to send
     * @param request The update request containing the chat information
     * @return The sent message
     * @throws TelegramApiException if sending the message fails
     */
    protected Message sendString(String string, UpdateRequest request)
        throws TelegramApiException {
        TelegramClient absSender = request.getAbsSender();
        Long chatId = request.getChatId();
        SendMessage sendMessage = new SendMessage(chatId.toString(), string);
        if (parseMode == null) parseMode = ParseMode.NO;
        sendMessage.setParseMode(parseMode.getValue());
        return absSender.execute(sendMessage);
    }
}
