package io.github.drednote.telegram.core.request;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import io.github.drednote.telegram.utils.Assert;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * The {@code AbstractUpdateRequest} class is an abstract implementation of the
 * {@link UpdateRequest} interface. It provides common functionality for processing update requests
 * received from Telegram.
 *
 * @author Ivan Galushko
 */
@Getter
public abstract class AbstractUpdateRequest implements UpdateRequest, UpdateRequestAccessor {

    /**
     * updateId
     */
    protected final Integer id;
    protected final Update origin;
    protected final Long chatId;
    @Nullable
    protected final Long userId;
    protected final RequestType requestType;

    protected final Set<MessageType> messageTypes;

    @Nullable
    protected final Message message;
    @Nullable
    protected final Chat chat;
    @Nullable
    protected final User user;
    @Nullable
    protected final String text;

    /**
     * Creates a new instance of the {@code AbstractUpdateRequest} class with the given update.
     *
     * @param update the update received from Telegram, not null
     */
    protected AbstractUpdateRequest(Update update) {
        Assert.notNull(update, "Update");

        this.origin = update;
        this.id = update.getUpdateId();

        this.message = firstNonNull(
            update.getMessage(), update.getEditedMessage(),
            update.getChannelPost(), update.getEditedChannelPost(),
            update.getBusinessMessage(), update.getEditedBuinessMessage()
        );

        if (message != null) {
            this.user = firstNonNull(message.getFrom(), message.getLeftChatMember(),
                message.getForwardFrom());
            this.chat = firstNonNull(message.getChat(), message.getForwardFromChat());
            this.text = firstNonNull(message.getText(), message.getCaption());
            this.requestType = parseMessageType(origin);
        } else if (update.getInlineQuery() != null) {
            this.user = update.getInlineQuery().getFrom();
            this.text = update.getInlineQuery().getQuery();
            this.chat = null;
            this.requestType = RequestType.INLINE_QUERY;
        } else if (update.getChosenInlineQuery() != null) {
            this.user = update.getChosenInlineQuery().getFrom();
            this.text = update.getChosenInlineQuery().getQuery();
            this.chat = null;
            this.requestType = RequestType.CHOSEN_INLINE_QUERY;
        } else if (update.getCallbackQuery() != null) {
            this.user = update.getCallbackQuery().getFrom();
            this.text = firstNonNull(update.getCallbackQuery().getData(),
                update.getCallbackQuery().getGameShortName());
            this.chat = null;
            this.requestType = RequestType.CALLBACK_QUERY;
        } else if (update.getShippingQuery() != null) {
            this.user = update.getShippingQuery().getFrom();
            this.text = update.getShippingQuery().getInvoicePayload();
            this.chat = null;
            this.requestType = RequestType.SHIPPING_QUERY;
        } else if (update.getPreCheckoutQuery() != null) {
            this.user = update.getPreCheckoutQuery().getFrom();
            this.text = update.getPreCheckoutQuery().getInvoicePayload();
            this.chat = null;
            this.requestType = RequestType.PRE_CHECKOUT_QUERY;
        } else if (update.getPoll() != null) {
            this.user = null;
            this.text = update.getPoll().getQuestion();
            this.chat = null;
            this.requestType = RequestType.POLL;
        } else if (update.getPollAnswer() != null) {
            this.user = update.getPollAnswer().getUser();
            this.text = null;
            this.chat = null;
            this.requestType = RequestType.POLL_ANSWER;
        } else if (update.getChatMember() != null) {
            this.user = update.getChatMember().getFrom();
            this.text = null;
            this.chat = update.getChatMember().getChat();
            this.requestType = RequestType.CHAT_MEMBER_UPDATED;
        } else if (update.getChatJoinRequest() != null) {
            this.user = update.getChatJoinRequest().getUser();
            this.text = null;
            this.chat = update.getChatJoinRequest().getChat();
            this.requestType = RequestType.CHAT_JOIN_REQUEST;
        } else if (update.getMessageReaction() != null) {
            this.user = update.getMessageReaction().getUser();
            this.text = null;
            this.chat = update.getMessageReaction().getChat();
            this.requestType = RequestType.MESSAGE_REACTION;
        } else if (update.getMessageReactionCount() != null) {
            this.user = null;
            this.text = null;
            this.chat = update.getMessageReactionCount().getChat();
            this.requestType = RequestType.MESSAGE_REACTION_COUNT;
        } else if (update.getChatBoost() != null) {
            this.user = null;
            this.text = null;
            this.chat = update.getChatBoost().getChat();
            this.requestType = RequestType.CHAT_BOOST;
        } else if (update.getRemovedChatBoost() != null) {
            this.user = null;
            this.text = null;
            this.chat = update.getRemovedChatBoost().getChat();
            this.requestType = RequestType.CHAT_BOOST_REMOVED;
        } else if (update.getBusinessConnection() != null) {
            this.user = update.getBusinessConnection().getUser();
            this.text = null;
            this.chat = null;
            this.requestType = RequestType.BUSINESS_CONNECTION;
        } else if (update.getDeletedBusinessMessages() != null) {
            this.user = null;
            this.text = null;
            this.chat = update.getDeletedBusinessMessages().getChat();
            this.requestType = RequestType.DELETED_BUSINESS_MESSAGE;
        } else if (update.getPaidMediaPurchased() != null) {
            this.user = update.getPaidMediaPurchased().getUser();
            this.text = null;
            this.chat = null;
            this.requestType = RequestType.PAID_MEDIA_PURCHASED;
        } else if(update.getMyChatMember() != null) {
            this.user = update.getMyChatMember().getFrom();
            this.text = null;
            this.chat = update.getMyChatMember().getChat();
            this.requestType = RequestType.MY_CHAT_MEMBER_UPDATED;
        }
        // this condition is unreachable
        else {
            throw new UnsupportedOperationException(
                "Cannot parse Update. One of optional parameters of Update must be present");
        }
        this.chatId = resolveChatId();
        this.messageTypes = parseMessageType();
        this.userId = user != null ? user.getId() : null;
    }

    private static RequestType parseMessageType(Update origin) {
        if (origin.getEditedMessage() != null) {
            return RequestType.EDITED_MESSAGE;
        } else if (origin.getChannelPost() != null) {
            return RequestType.CHANEL_POST;
        } else if (origin.getEditedChannelPost() != null) {
            return RequestType.EDITED_CHANEL_POST;
        } else if (origin.getBusinessMessage() != null) {
            return RequestType.BUSINESS_MESSAGE;
        } else if (origin.getEditedBuinessMessage() != null) {
            return RequestType.EDITED_BUSINESS_MESSAGE;
        } else {
            return RequestType.MESSAGE;
        }
    }

    /**
     * Create new instance of {@code AbstractUpdateRequest} class based on an existing
     * {@code UpdateRequest}
     *
     * @param request existing {@code UpdateRequest}
     */
    protected AbstractUpdateRequest(UpdateRequest request) {
        Assert.notNull(request, "UpdateRequest");
        this.id = request.getId();
        this.origin = request.getOrigin();
        this.chatId = request.getChatId();
        this.requestType = request.getRequestType();
        this.messageTypes = request.getMessageTypes();
        this.message = request.getMessage();
        this.chat = request.getChat();
        this.user = request.getUser();
        this.text = request.getText();
        this.userId = request.getUserId();
    }

    @NonNull
    private Set<MessageType> parseMessageType() {
        if (message == null) {
            return Collections.emptySet();
        }
        Set<MessageType> types = EnumSet.noneOf(MessageType.class);
        if (message.isCommand()) {
            types.add(MessageType.COMMAND);
        } else if (message.hasText()) {
            types.add(MessageType.TEXT);
        }
        if (message.hasAudio()) {
            types.add(MessageType.AUDIO);
        }
        if (message.hasDocument()) {
            types.add(MessageType.DOCUMENT);
        }
        if (message.hasPhoto()) {
            types.add(MessageType.PHOTO);
        }
        if (message.hasSticker()) {
            types.add(MessageType.STICKER);
        }
        if (message.hasVideo()) {
            types.add(MessageType.VIDEO);
        }
        if (message.hasContact()) {
            types.add(MessageType.CONTACT);
        }
        if (message.hasLocation()) {
            types.add(MessageType.LOCATION);
        }
        if (message.getVenue() != null) {
            types.add(MessageType.VENUE);
        }
        if (message.hasAnimation()) {
            types.add(MessageType.ANIMATION);
        }
        if (message.getReplyToMessage() != null) {
            types.add(MessageType.REPLY_TO_MESSAGE);
        }
        if (message.hasVoice()) {
            types.add(MessageType.VOICE);
        }
        if (message.hasGame()) {
            types.add(MessageType.GAME);
        }
        if (message.hasInvoice()) {
            types.add(MessageType.INVOICE);
        }
        if (message.hasSuccessfulPayment()) {
            types.add(MessageType.SUCCESSFUL_PAYMENT);
        }
        if (message.hasVideoNote()) {
            types.add(MessageType.VIDEO_NOTE);
        }
        if (message.hasPoll()) {
            types.add(MessageType.POLL);
        }
        if (message.hasDice()) {
            types.add(MessageType.DICE);
        }
        if (message.hasStory()) {
            types.add(MessageType.STORY);
        }
        if (message.getExternalReplyInfo() != null) {
            types.add(MessageType.EXTERNAL_REPLY_INFO);
        }
        if (message.getForwardOrigin() != null) {
            types.add(MessageType.FORWARD_ORIGIN);
        }
        if (message.getQuote() != null) {
            types.add(MessageType.QUOTE);
        }
        if (message.getGiveaway() != null) {
            types.add(MessageType.GIVEAWAY);
        }
        if (message.getGiveawayWinners() != null) {
            types.add(MessageType.GIVEAWAY_WINNERS);
        }
        if (message.hasReplyToStory()) {
            types.add(MessageType.REPLY_TO_STORY);
        }
        if (message.hasBoostAdded()) {
            types.add(MessageType.BOOST_ADDED);
        }
        if (message.hasPaidMedia()) {
            types.add(MessageType.PAID_MEDIA);
        }

        parseChatChanges(types);
        parseServiceMessage(types);

        if (types.isEmpty()) {
            types.add(MessageType.UNKNOWN);
        }

        return types;
    }

    private void parseServiceMessage(Set<MessageType> types) {
        if (message != null && firstNonNull(
            message.getProximityAlertTriggered(), message.getMessageAutoDeleteTimerChanged(),
            message.getWebAppData(), message.getVideoChatStarted(), message.getVideoChatEnded(),
            message.getVideoChatParticipantsInvited(), message.getVideoChatScheduled(),
            message.getForumTopicCreated(), message.getForumTopicClosed(),
            message.getForumTopicReopened(), message.getForumTopicEdited(),
            message.getGeneralForumTopicHidden(), message.getGeneralForumTopicUnhidden(),
            message.getWriteAccessAllowed(), message.getUserShared(), message.getChatShared(),
            message.getGiveawayCreated(), message.getGiveawayCompleted(), message.getBoostAdded(),
            message.getChatBackgroundSet(), message.getRefundedPayment(), message.getGift(),
            message.getUniqueGift(), message.getPaidMessagePriceChanged()

        ) != null) {
            types.add(MessageType.SERVICE_MESSAGE);
        }
    }

    private void parseChatChanges(Set<MessageType> types) {
        if (message != null &&
            (message.getPinnedMessage() != null ||
             !CollectionUtils.isEmpty(message.getNewChatMembers()) ||
             message.getLeftChatMember() != null ||
             message.getNewChatTitle() != null ||
             message.getNewChatPhoto() != null ||
             message.getDeleteChatPhoto() != null ||
             message.getGroupchatCreated() != null
            )
        ) {
            types.add(MessageType.CHAT_CHANGES);
        }
    }

    private Long resolveChatId() {
        if (chat != null) {
            return chat.getId();
        } else if (user != null) {
            return user.getId();
        } else {
            return Long.valueOf(id);
        }
    }

    @Override
    public UpdateRequestAccessor getAccessor() {
        return this;
    }

    @NonNull
    public Set<MessageType> getMessageTypes() {
        return messageTypes.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(messageTypes);
    }
}
