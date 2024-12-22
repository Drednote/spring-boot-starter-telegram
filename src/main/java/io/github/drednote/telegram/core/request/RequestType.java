package io.github.drednote.telegram.core.request;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The {@code RequestType} enum represents the type of {@link Update}.
 *
 * @author Ivan Galushko
 * @apiNote Only one type can be present for any given update.
 * @see Update
 */
public enum RequestType {
    /**
     * Represents a message of update
     *
     * @see Update#getMessage()
     */
    MESSAGE,
    /**
     * Represents an edited message of update
     *
     * @see Update#getEditedMessage()
     */
    EDITED_MESSAGE,
    /**
     * Represents a channel post of update
     *
     * @see Update#getChannelPost()
     */
    CHANEL_POST,
    /**
     * Represents an edited channel post of update
     *
     * @see Update#getEditedChannelPost()
     */
    EDITED_CHANEL_POST,
    /**
     * Represents a business message of update
     *
     * @see Update#getBusinessMessage()
     */
    BUSINESS_MESSAGE,
    /**
     * Represents an edited business message of update
     *
     * @see Update#getEditedBuinessMessage()
     */
    EDITED_BUSINESS_MESSAGE,
    /**
     * Represents an inline query of update
     *
     * @see Update#getInlineQuery()
     */
    INLINE_QUERY,
    /**
     * Represents a chosen inline query update
     *
     * @see Update#getChosenInlineQuery()
     */
    CHOSEN_INLINE_QUERY,
    /**
     * Represents a callback query update
     *
     * @see Update#getCallbackQuery()
     */
    CALLBACK_QUERY,
    /**
     * Represents a shipping query update
     *
     * @see Update#getShippingQuery()
     */
    SHIPPING_QUERY,
    /**
     * Represents a pre-checkout query update
     *
     * @see Update#getPreCheckoutQuery()
     */
    PRE_CHECKOUT_QUERY,
    /**
     * Represents a poll update
     *
     * @see Update#getPoll()
     */
    POLL,
    /**
     * Represents a poll answer update
     *
     * @see Update#getPollAnswer()
     */
    POLL_ANSWER,
    /**
     * Represents a chat member updated update
     *
     * @see Update#getChatMember()
     */
    CHAT_MEMBER_UPDATED,
    /**
     * Represents a chat join request update
     *
     * @see Update#getChatJoinRequest()
     */
    CHAT_JOIN_REQUEST,
    /**
     * A reaction to a message was changed by a user
     *
     * @see Update#getMessageReaction()
     */
    MESSAGE_REACTION,
    /**
     * Reactions to a message with anonymous reactions were changed
     *
     * @see Update#getMessageReactionCount()
     */
    MESSAGE_REACTION_COUNT,
    /**
     * Represents a boost added to a chat or changed.
     *
     * @see Update#getChatBoost()
     */
    CHAT_BOOST,
    /**
     * A boost was removed from a chat.
     *
     * @see Update#getRemovedChatBoost()
     */
    CHAT_BOOST_REMOVED,
    /**
     * The bot was connected to or disconnected from a business account, or a user edited an
     * existing connection with the bot.
     *
     * @see Update#getBusinessConnection()
     */
    BUSINESS_CONNECTION,
    /**
     * Messages were deleted from a connected business account
     *
     * @see Update#getDeletedBusinessMessages()
     */
    DELETED_BUSINESS_MESSAGE,
    /**
     * A user purchased paid media with a non-empty payload sent by the bot in a non-channel chat
     *
     * @see Update#getPaidMediaPurchased()
     */
    PAID_MEDIA_PURCHASED,
    /**
     * Bot was added in a chat
     */
    ENTERED_CHAT,
    /**
     * Bot was deleted from a chat
     */
    LEFT_CHAT
}
