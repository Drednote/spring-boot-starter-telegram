package io.github.drednote.telegram.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.resolver.TelegramResponseTypesResolver;
import io.github.drednote.telegram.utils.Assert;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A general-purpose implementation of {@link TelegramResponse} capable of handling and processing a wide variety of
 * response types returned by update handlers.
 * <p>
 * Supported response types include:
 * <ul>
 *     <li>{@link TelegramResponse} (returned as-is)</li>
 *     <li>{@link Collection} of responses (wrapped in {@link CompositeTelegramResponse})</li>
 *     <li>{@link Flux} (wrapped in {@link FluxTelegramResponse})</li>
 *     <li>{@link Mono} (wrapped in {@link FluxTelegramResponse})</li>
 *     <li>{@link Stream} (wrapped in {@link StreamTelegramResponse})</li>
 *     <li>{@link BotApiMethod} (send to telegram)</li>
 *     <li>{@link String} and {@code byte[]} (sent as messages)</li>
 *     <li>Any POJO (if {@code serializeJavaObjectWithJackson} is enabled)</li>
 * </ul>
 * <p>
 * This class is designed to be a fallback/default implementation when a response type
 * is not a known concrete {@code TelegramResponse}.
 *
 * @author Ivan Galushko
 * @see TelegramResponseHelper
 * @see TelegramResponseTypesResolver
 */
public class GenericTelegramResponse extends AbstractTelegramResponse {

    /**
     * The response object to be processed
     */
    private final Object response;

    /**
     * Creates a new instance of GenericTelegramResponse with the specified response object.
     *
     * @param response The response object to be processed
     */
    public GenericTelegramResponse(@Nullable Object response) {
        if (response == null || Void.TYPE.isAssignableFrom(response.getClass())) {
            this.response = EmptyTelegramResponse.INSTANCE;
        } else {
            this.response = response;
        }
    }

    /**
     * Processes the Telegram response. Depending on the type of response, it will be sent to the chat.
     *
     * @param request The UpdateRequest containing the update and sender information
     * @throws TelegramApiException if an error occurs while sending the response
     */
    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        Assert.notNull(request, "UpdateRequest");

        TelegramResponseHelper helper = getTelegramResponseHelper();

        if (helper != null) {
            helper.propagateProperties(this).process(request);
        } else {
            processGenericType(request);
        }
    }

    @Override
    public Mono<Void> processReactive(UpdateRequest request) {
        Assert.notNull(request, "UpdateRequest");

        TelegramResponseHelper helper = getTelegramResponseHelper();

        if (helper != null) {
            return helper.propagateProperties(this).processReactive(request);
        } else {
            return Mono.fromCallable(() -> {
                processGenericType(request);
                return null;
            });
        }
    }

    @Nullable
    private TelegramResponseHelper getTelegramResponseHelper() {
        TelegramResponseHelper helper = null;

        if (resolver != null) {
            TelegramResponse resolved = resolver.resolve(response);
            if (resolved != null) {
                helper = TelegramResponseHelper.create(resolved);
            }
        }
        if (helper == null) {
            if (response instanceof TelegramResponse telegramResponse) {
                helper = TelegramResponseHelper.create(telegramResponse);
            } else if (response instanceof Collection<?> collection) {
                helper = TelegramResponseHelper.create(new CompositeTelegramResponse(collection));
            } else if (response instanceof Flux<?> flux) {
                helper = TelegramResponseHelper.create(new FluxTelegramResponse(flux));
            } else if (response instanceof Mono<?> mono) {
                helper = TelegramResponseHelper.create(new FluxTelegramResponse(mono.flux()));
            } else if (response instanceof Stream<?> stream) {
                helper = TelegramResponseHelper.create(new StreamTelegramResponse(stream));
            }
        }
        return helper;
    }

    private void processGenericType(UpdateRequest request) throws TelegramApiException {
        Object responseMessage;
        if (response instanceof String str) {
            responseMessage = sendString(str, request);
        } else if (response instanceof byte[] bytes) {
            responseMessage = sendString(new String(bytes, StandardCharsets.UTF_8), request);
        } else if (response instanceof BotApiMethod<?> botApiMethod) {
            responseMessage = request.getTelegramClient().execute(botApiMethod);
        } else if (isSendBotApiMethod(request.getTelegramClient())) {
            responseMessage = tryToSendBotApiMethod(request.getTelegramClient());
        } else if (objectMapper != null && Boolean.TRUE.equals(serializeJavaObjectWithJackson)) {
            try {
                String stringResponse = objectMapper.writeValueAsString(response);
                String truncated = truncateQuotes(stringResponse);
                responseMessage = sendString(truncated, request);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Cannot serialize response", e);
            }
        } else {
            throw new IllegalStateException(
                "Cannot process response. Unsupported type %s".formatted(response.getClass()));
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

    boolean isSendBotApiMethod(TelegramClient client) {
        try {
            client.getClass().getMethod("execute", response.getClass());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Nullable
    private Object tryToSendBotApiMethod(TelegramClient client) throws TelegramApiException {
        try {
            Method execute = client.getClass().getMethod("execute", response.getClass());
            return execute.invoke(client, response);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new TelegramApiException(cause);
        }
    }

    @Nullable
    public Object getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "GenericTelegramResponse{ response=" + response + '}';
    }
}
