package io.github.drednote.telegram.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.UpdateHandlerProperties.ParseMode;
import io.github.drednote.telegram.response.resolver.TelegramResponseTypesResolver;
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

    @Nullable
    protected ParseMode parseMode = null;
    @Nullable
    protected TelegramResponseTypesResolver resolver = null;
    @Nullable
    protected TelegramProperties telegramProperties = null;
    @Nullable
    protected ObjectMapper objectMapper = null;

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
        TelegramClient absSender = request.getTelegramClient();
        Long chatId = request.getChatId();
        SendMessage sendMessage = new SendMessage(chatId.toString(), string);
        if (parseMode != null) {
            sendMessage.setParseMode(parseMode.getValue());
        }
        return absSender.execute(sendMessage);
    }

    protected TelegramResponse wrapWithTelegramResponse(Object response) {
        return response instanceof TelegramResponse res ? res : new GenericTelegramResponse(response);
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    @Nullable
    public ParseMode getParseMode() {
        return parseMode;
    }


    public void setResolver(TelegramResponseTypesResolver resolver) {
        this.resolver = resolver;
    }

    @Nullable
    public TelegramResponseTypesResolver getResolver() {
        return resolver;
    }

    public void setTelegramProperties(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @Nullable
    public TelegramProperties getTelegramProperties() {
        return telegramProperties;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
