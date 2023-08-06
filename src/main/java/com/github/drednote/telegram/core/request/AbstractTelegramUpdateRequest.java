package com.github.drednote.telegram.core.request;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.github.drednote.telegram.utils.Assert;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
public abstract class AbstractTelegramUpdateRequest implements ExtendedTelegramUpdateRequest {

  /**
   * updateId
   */
  protected final Integer id;
  protected final Update origin;
  /**
   * chatId == userId
   */
  protected final Long chatId;
  protected final RequestType requestType;

  @NonNull
  protected final Set<MessageType> messageTypes;

  @Nullable
  protected final Message message;
  @Nullable
  protected final Chat chat;
  @Nullable
  protected final User user;
  @Nullable
  protected final String text;

  protected AbstractTelegramUpdateRequest(@NonNull Update update) {
    Assert.notNull(update, "update");
    this.origin = update;
    this.id = update.getUpdateId();

    this.message = firstNonNull(update.getMessage(),
        update.getEditedMessage(),
        update.getChannelPost(),
        update.getEditedChannelPost());

    if (message != null) {
      this.user = firstNonNull(message.getFrom(), message.getLeftChatMember(),
          message.getForwardFrom());
      this.chat = firstNonNull(message.getChat(), message.getForwardFromChat());
      this.text = firstNonNull(message.getText(), message.getCaption());
      this.requestType = RequestType.MESSAGE;
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
      this.text = update.getCallbackQuery().getData();
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
    }
    // this condition is unreachable
    else {
      throw new UnsupportedOperationException(
          "Cannot parse Update. One of optional parameters of Update must be present");
    }
    this.chatId = resolveChatId();
    this.messageTypes = parseMessageType();
  }

  protected AbstractTelegramUpdateRequest(AbstractTelegramUpdateRequest request) {
    this.id = request.id;
    this.origin = request.origin;
    this.chatId = request.chatId;
    this.requestType = request.requestType;
    this.messageTypes = request.messageTypes;
    this.message = request.message;
    this.chat = request.chat;
    this.user = request.user;
    this.text = request.text;
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
    if (message.getGame() != null) {
      types.add(MessageType.GAME);
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
    parseChatChanges(types);
    parseServiceMessage(types);
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
        message.getWriteAccessAllowed(), message.getUserShared(), message.getChatShared()
    ) != null) {
      types.add(MessageType.SERVICE_MESSAGE);
    }
  }

  private void parseChatChanges(Set<MessageType> types) {
    if (message != null &&
        (message.getPinnedMessage() != null ||
            message.getNewChatMembers() != null ||
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

  @NonNull
  public Set<MessageType> getMessageTypes() {
    return EnumSet.copyOf(messageTypes);
  }
}
