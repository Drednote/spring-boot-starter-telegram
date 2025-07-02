package io.github.drednote.telegram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.response.TelegramResponse;
import java.util.List;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * The {@code UpdateRequest} interface represents a request received from
 * <a href="https://core.telegram.org/bots/api">Telegram API</a> as an {@link Update}.
 * Implementations of this interface provide getters and setters to access and modify the various properties of the
 * request.
 *
 * @author Ivan Galushko
 */
public interface UpdateRequest {

    //-----------getters for Update-----------

    /**
     * Returns the ID of the update
     *
     * @return the ID of the update
     */
    @NonNull
    Integer getId();

    /**
     * Returns the ID of the chat associated with the update
     *
     * @return the ID of the chat
     * @apiNote chatId can be equals userId
     */
    @NonNull
    Long getChatId();

    /**
     * Returns the ID of the user associated with the update
     *
     * @return the ID of the user
     */
    @Nullable
    Long getUserId();

    /**
     * This ID is needed to identify a unique user based on ChatId and UserId. If UserId is null, then will be returned
     * only to ChatId.
     *
     * @return unique user ID
     */
    default String getUserAssociatedId() {
        return getUserId() == null ? getChatId().toString() : getUserId() + "_" + getChatId();
    }

    /**
     * Returns the type of the request
     *
     * @return the type of the request
     */
    @NonNull
    RequestType getRequestType();

    /**
     * Returns the types of message contained in the request
     * <p>
     * In case if a type of message cannot correctly be determined, then will be return an empty set Typically, this
     * shows something went wrong
     *
     * @return the types of message, or an empty set if the request is not a message or the message types cannot be
     * determined
     */
    @NonNull
    Set<MessageType> getMessageTypes();

    /**
     * Returns the text of the message or of the request
     * <p>
     * if {@link #getRequestType()} == {@link RequestType#MESSAGE} and a field 'text' is empty, than can be field
     * 'caption' if it presents
     *
     * @return the text of the message or of the request. Return null if {@link Update} has no text
     * @see AbstractUpdateRequest#AbstractUpdateRequest(Update)
     */
    @Nullable
    String getText();

    /**
     * Returns the message associated with the request
     *
     * @return the message, or null if not applicable
     * @see Update#getMessage()
     * @see Update#getEditedMessage()
     * @see Update#getChannelPost()
     * @see Update#getEditedChannelPost()
     * @see AbstractUpdateRequest#AbstractUpdateRequest(Update)
     */
    @Nullable
    Message getMessage();

    /**
     * Returns the chat associated with the request
     *
     * @return the chat, or null if not applicable
     * @see AbstractUpdateRequest#AbstractUpdateRequest(Update)
     */
    @Nullable
    Chat getChat();

    /**
     * Returns the user associated with the request
     *
     * @return the user, or null if not applicable
     * @see AbstractUpdateRequest#AbstractUpdateRequest(Update)
     */
    @Nullable
    User getUser();

    /**
     * Returns the original update received from Telegram
     *
     * @return the original update
     * @see Update
     */
    @NonNull
    Update getOrigin();

    //-----------other getters-----------

    /**
     * Returns the abstract sender used to send responses
     *
     * @return the abstract sender
     */
    @NonNull
    TelegramClient getAbsSender();

    /**
     * Returns the permission of the user executing the request
     *
     * @return user's permission
     * @apiNote Can be null only before {@link RoleFilter} execution
     */
    @Nullable
    Permission getPermission();

    /**
     * Returns the scenario associated with the request
     *
     * @return the scenario, or null if no scenario belongs to given chat
     * @see ScenarioUpdateHandler
     * @see ScenarioConfigurerAdapter
     */
    @Nullable
    Scenario<?> getScenario();

    /**
     * Returns the response that should be sent to Telegram
     *
     * @return the response, or null if no one {@link UpdateHandler} or
     * {@link PreUpdateFilter}/{@link PostUpdateFilter}/{@link ConclusivePostUpdateFilter} set response
     * @see ResponseSetter
     */
    @Nullable
    TelegramResponse getResponse();

    /**
     * This field contains responses from the telegram. It may be empty if there was no response or the response to the
     * telegram itself has not been sent yet.
     *
     * @return a list of telegram responses.
     */
    @NonNull
    List<Object> getResponseFromTelegram();

    /**
     * Returns the error that occurred during the processing of the request, if any
     *
     * @return the error, or null if no error occurred
     */
    @Nullable
    Throwable getError();

    /**
     * Returns the properties specific to the Telegram configuration
     *
     * @return the Telegram properties
     */
    @NonNull
    TelegramProperties getProperties();

    /**
     * Returns the info for invocation {@link TelegramRequest} methods
     *
     * @return the info for invocation, or null if not found methods
     * @see ControllerUpdateHandler
     */
    @Nullable
    RequestHandler getRequestHandler();

    /**
     * Returns the object mapper used for JSON serialization and deserialization
     *
     * @return the object mapper
     */
    @NonNull
    ObjectMapper getObjectMapper();

    /**
     * <b>Normally you should not use this method, all methods in {@code UpdateRequestAccessor} are internal. If you
     * will use them, unexpected behavior can happen.</b>
     *
     * @return accessor
     */
    UpdateRequestAccessor getAccessor();

}
