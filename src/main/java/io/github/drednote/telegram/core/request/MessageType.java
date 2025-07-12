package io.github.drednote.telegram.core.request;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * The {@code MessageType} enum represents the type of {@link Update}'s message.
 * <p>
 * If during parsing, the message type is not determined, it sets to default value - {@code UNKNOWN}
 *
 * @author Ivan Galushko
 * @see Update#getMessage
 */
public enum MessageType {

    /**
     * Represents a text message.
     *
     * @see Message#getText()
     */
    TEXT,

    /**
     * Represents a command message.
     *
     * @apiNote <b>Note that the command is not a text</b>
     * @see Message#isCommand()
     */
    COMMAND,

    /**
     * Represents an audio message.
     *
     * @see Message#getAudio()
     */
    AUDIO,

    /**
     * Represents a document message.
     *
     * @see Message#getDocument()
     */
    DOCUMENT,

    /**
     * Represents a photo message.
     *
     * @see Message#getPhoto()
     */
    PHOTO,

    /**
     * Represents a sticker message.
     *
     * @see Message#getSticker()
     */
    STICKER,

    /**
     * Represents a video message.
     *
     * @see Message#getVideo()
     */
    VIDEO,

    /**
     * Represents a contact message.
     *
     * @see Message#getContact()
     */
    CONTACT,

    /**
     * Represents a location message.
     *
     * @see Message#getLocation()
     */
    LOCATION,

    /**
     * Represents a venue message.
     *
     * @see Message#getVenue()
     */
    VENUE,

    /**
     * Represents an animation message.
     *
     * @see Message#getAnimation()
     */
    ANIMATION,

    /**
     * Represents a message with changes in the chat.
     *
     * @see Message#getPinnedMessage()
     * @see Message#getNewChatMembers()
     * @see Message#getLeftChatMember()
     * @see Message#getNewChatTitle()
     * @see Message#getNewChatPhoto()
     * @see Message#getDeleteChatPhoto()
     * @see Message#getGroupchatCreated()
     */
    CHAT_CHANGES,

    /**
     * Represents a reply to a message.
     *
     * @see Message#getReplyToMessage()
     */
    REPLY_TO_MESSAGE,

    /**
     * Represents a voice message.
     *
     * @see Message#getVoice()
     */
    VOICE,

    /**
     * Represents a game message.
     *
     * @see Message#getGame()
     */
    GAME,

    /**
     * Represents a successful payment message.
     *
     * @see Message#getSuccessfulPayment()
     */
    SUCCESSFUL_PAYMENT,

    /**
     * Represents a video note message.
     *
     * @see Message#getVideoNote()
     */
    VIDEO_NOTE,

    /**
     * Represents a poll message.
     *
     * @see Message#getPoll()
     */
    POLL,

    /**
     * Represents a dice message.
     *
     * @see Message#getDice()
     */
    DICE,

    /**
     * @see Message#getInvoice()
     */
    INVOICE,
    /**
     * @see Message#getStory()
     */
    STORY,
    /**
     * @see Message#getExternalReplyInfo()
     */
    EXTERNAL_REPLY_INFO,
    /**
     * @see Message#getForwardOrigin()
     */
    FORWARD_ORIGIN,
    /**
     * @see Message#getQuote()
     */
    QUOTE,
    /**
     * @see Message#getGiveaway()
     */
    GIVEAWAY,
    /**
     * @see Message#getGiveawayWinners()
     */
    GIVEAWAY_WINNERS,
    /**
     * @see Message#getReplyToStory()
     */
    REPLY_TO_STORY,
    /**
     * @see Message#getBoostAdded()
     */
    BOOST_ADDED,
    /**
     * @see Message#getPaidMedia()
     */
    PAID_MEDIA,

    /**
     * If during parsing, the message type is not determined, it sets to default value - {@code UNKNOWN}
     */
    UNKNOWN,

    /**
     * Represents a service message.
     *
     * @see Message#getProximityAlertTriggered()
     * @see Message#getMessageAutoDeleteTimerChanged()
     * @see Message#getWebAppData()
     * @see Message#getVideoChatStarted()
     * @see Message#getVideoChatEnded()
     * @see Message#getVideoChatParticipantsInvited()
     * @see Message#getVideoChatScheduled()
     * @see Message#getForumTopicCreated()
     * @see Message#getForumTopicClosed()
     * @see Message#getForumTopicReopened()
     * @see Message#getForumTopicEdited()
     * @see Message#getGeneralForumTopicHidden()
     * @see Message#getGeneralForumTopicUnhidden()
     * @see Message#getWriteAccessAllowed()
     * @see Message#getUserShared()
     * @see Message#getChatShared()
     */
    SERVICE_MESSAGE
}
