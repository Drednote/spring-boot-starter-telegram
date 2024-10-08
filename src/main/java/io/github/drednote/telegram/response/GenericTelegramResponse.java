package io.github.drednote.telegram.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This class represents a generic Telegram response handler that can process various types of
 * responses and messages. It extends the {@code AbstractTelegramResponse} class and implements the
 * {@link TelegramResponse} interface. The response can be a String, byte array, BotApiMethod,
 * SendMediaBotMethod, TelegramResponse, or any serializable object. Depending on the type of
 * response, it will be processed accordingly.
 * <p>
 * It is the main {@code TelegramResponse} implementation that should be used manually in code
 *
 * @author Ivan Galushko
 * @implNote If you pass {@link BotApiMethod} or {@link SendMediaBotMethod} as a 'response' of this
 * class, the 'chatId' property will be automatically set during sending (only if it is null). If
 * you manually set 'chatId', nothing happens
 */
public class GenericTelegramResponse extends AbstractTelegramResponse {

    private static final String CHAT_ID = "chatId";
    private static final String PARSE_MODE = "parseMode";

    /**
     * The response object to be processed
     */
    private final Object response;

    /**
     * Creates a new instance of GenericTelegramResponse with the specified response object.
     *
     * @param response The response object to be processed
     */
    public GenericTelegramResponse(@NonNull Object response) {
        Assert.required(response, "Response");
        this.response = response;
    }

    /**
     * Processes the Telegram response. Depending on the type of response, it will be sent to the
     * chat.
     *
     * @param request The UpdateRequest containing the update and sender information
     * @throws TelegramApiException if an error occurs while sending the response
     */
    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        Assert.notNull(request, "UpdateRequest");
        Serializable responseMessage;
        if (response instanceof String str) {
            responseMessage = sendString(str, request);
        } else if (response instanceof byte[] bytes) {
            responseMessage = sendString(new String(bytes, StandardCharsets.UTF_8), request);
        } else if (response instanceof BotApiMethod<?> botApiMethod) {
            responseMessage = request.getAbsSender().execute(botApiMethod);
        } else if (response instanceof SendMediaBotMethod<?>) {
            responseMessage = tryToSendMedia(request);
        } else if (response instanceof TelegramResponse telegramResponse) {
            telegramResponse.process(request);
            responseMessage = null;
        } else if (response instanceof Collection<?> collection) {
            convertCollectionToResponse(collection).process(request);
            responseMessage = null;
        } else if (request.getProperties().getUpdateHandler().isSerializeJavaObjectWithJackson()) {
            try {
                String stringResponse = request.getObjectMapper().writeValueAsString(response);
                String truncated = truncateQuotes(stringResponse);
                responseMessage = sendString(truncated, request);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Cannot serialize response", e);
            }
        } else {
            throw new IllegalStateException("Cannot process response %s".formatted(response));
        }
        if (responseMessage != null) {
            request.getAccessor().addResponseFromTelegram(responseMessage);
        }
    }

    private String truncateQuotes(String stringResponse) {
        if (!StringUtils.isBlank(stringResponse) && (stringResponse.startsWith("\"")
                                                     && stringResponse.endsWith("\""))) {
            return stringResponse.substring(1, stringResponse.length() - 1);
        }
        return stringResponse;
    }

    @Nullable
    private Serializable tryToSendMedia(UpdateRequest request) throws TelegramApiException {
        if (response instanceof SendAnimation sendAnimation) {
            return request.getAbsSender().execute(sendAnimation);
        }
        if (response instanceof SendAudio sendAudio) {
            return request.getAbsSender().execute(sendAudio);
        }
        if (response instanceof SendDocument sendDocument) {
            return request.getAbsSender().execute(sendDocument);
        }
        if (response instanceof SendPhoto sendPhoto) {
            return request.getAbsSender().execute(sendPhoto);
        }
        if (response instanceof SendSticker sendSticker) {
            return request.getAbsSender().execute(sendSticker);
        }
        if (response instanceof SendVideo sendVideo) {
            return request.getAbsSender().execute(sendVideo);
        }
        if (response instanceof SendVideoNote sendVideoNote) {
            return request.getAbsSender().execute(sendVideoNote);
        }
        if (response instanceof SendVoice sendVoice) {
            return request.getAbsSender().execute(sendVoice);
        }
        return null;
    }

    private static CompositeTelegramResponse convertCollectionToResponse(Collection<?> invoked) {
        Collection<TelegramResponse> responses = new ArrayList<>();
        for (Object o : invoked) {
            if (o instanceof TelegramResponse telegramResponse) {
                responses.add(telegramResponse);
            } else {
                responses.add(new GenericTelegramResponse(o));
            }
        }
        return new CompositeTelegramResponse(responses);
    }

    public Object getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "GenericTelegramResponse{ response=" + response + '}';
    }
}
