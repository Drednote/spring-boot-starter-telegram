package com.github.drednote.telegram.core.request;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.github.drednote.telegram.utils.Assert;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public abstract class AbstractBotRequest implements ExtendedBotRequest {

  /**
   * updateId
   */
  @Getter
  protected final Integer id;
  @Getter
  protected final Update origin;
  /**
   * chatId == userId
   */
  @Getter
  protected final Long chatId;
  @Getter
  protected final RequestType messageType;

  @Getter
  @Nullable
  protected final Message message;
  @Getter
  @Nullable
  protected final Chat chat;
  @Getter
  @Nullable
  protected final User user;
  @Getter
  @Nullable
  protected final String text;

  protected AbstractBotRequest(@NonNull Update update) {
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
      this.text = message.getText();
      if (message.isCommand()) {
        this.messageType = RequestType.COMMAND;
      } else {
        this.messageType = RequestType.MESSAGE;
      }
    } else if (update.getInlineQuery() != null) {
      this.user = update.getInlineQuery().getFrom();
      this.text = update.getInlineQuery().getQuery();
      this.chat = null;
      this.messageType = RequestType.INLINE_QUERY;
    } else if (update.getChosenInlineQuery() != null) {
      this.user = update.getChosenInlineQuery().getFrom();
      this.text = update.getChosenInlineQuery().getQuery();
      this.chat = null;
      this.messageType = RequestType.CHOSEN_INLINE_QUERY;
    } else if (update.getCallbackQuery() != null) {
      this.user = update.getCallbackQuery().getFrom();
      this.text = update.getCallbackQuery().getData();
      this.chat = null;
      this.messageType = RequestType.CALLBACK_QUERY;
    } else if (update.getShippingQuery() != null) {
      this.user = update.getShippingQuery().getFrom();
      this.text = update.getShippingQuery().getInvoicePayload();
      this.chat = null;
      this.messageType = RequestType.SHIPPING_QUERY;
    } else if (update.getPreCheckoutQuery() != null) {
      this.user = update.getPreCheckoutQuery().getFrom();
      this.text = update.getPreCheckoutQuery().getInvoicePayload();
      this.chat = null;
      this.messageType = RequestType.PRE_CHECKOUT_QUERY;
    } else if (update.getPoll() != null) {
      this.user = null;
      this.text = update.getPoll().getQuestion();
      this.chat = null;
      this.messageType = RequestType.POLL;
    } else if (update.getPollAnswer() != null) {
      this.user = update.getPollAnswer().getUser();
      this.text = null;
      this.chat = null;
      this.messageType = RequestType.POLL_ANSWER;
    } else if (update.getChatMember() != null) {
      this.user = update.getChatMember().getFrom();
      this.text = null;
      this.chat = update.getChatMember().getChat();
      this.messageType = RequestType.CHAT_MEMBER_UPDATED;
    } else if (update.getChatJoinRequest() != null) {
      this.user = update.getChatJoinRequest().getUser();
      this.text = null;
      this.chat = update.getChatJoinRequest().getChat();
      this.messageType = RequestType.CHAT_JOIN_REQUEST;
    }
    // this condition is unreachable
    else {
      throw new UnsupportedOperationException(
          "Cannot parse Update. One of optional parameters of Update must be present");
    }
    this.chatId = resolveChatId();
  }

  protected AbstractBotRequest(AbstractBotRequest request) {
    this.id = request.id;
    this.origin = request.origin;
    this.chatId = request.chatId;
    this.messageType = request.messageType;
    this.message = request.message;
    this.chat = request.chat;
    this.user = request.user;
    this.text = request.text;
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
}
