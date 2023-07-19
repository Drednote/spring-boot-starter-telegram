package com.github.drednote.telegram.core;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Getter
public sealed class UpdateRequest permits ImmutableUpdateRequest {

  private final Integer id;
  private final Update origin;
  @JsonIgnore
  private final AbsSender absSender;
  private final Long chatId;
  private final RequestType messageType;
  @JsonIgnore
  private final TelegramProperties properties;

  @Nullable
  private final Message message;
  @Nullable
  private final Chat chat;
  @Nullable
  private final User user;
  @Nullable
  private final String text;

  @Setter
  @NonNull
  private Permission permission;

  // mvc
  @Setter
  @Nullable
  @JsonIgnore
  private HandlerMethod handlerMethod;
  @Setter
  @Nullable
  @JsonIgnore
  private Map<String, String> templateVariables;
  @Setter
  @Nullable
  @JsonIgnore
  private String basePattern;

  // scenario
  @Setter
  @JsonIgnore
  private Object state;

  // response
  @Setter
  @Nullable
  @JsonIgnore
  private HandlerResponse response;
  @Setter
  @JsonIgnore
  private ObjectMapper objectMapper;

  /**
   * If error occurred during update handling
   */
  @Nullable
  @Setter
  private Throwable error;

  public UpdateRequest(@NonNull Update update, AbsSender absSender, TelegramProperties properties) {
    this.origin = update;
    this.absSender = absSender;
    this.id = update.getUpdateId();
    this.properties = properties;

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
      this.messageType = null;
      this.user = null;
      this.text = null;
      this.chat = null;
    }
    if (chat != null) {
      chatId = chat.getId();
    } else if (user != null) {
      chatId = user.getId();
    } else {
      chatId = Long.valueOf(update.getUpdateId());
    }
  }

  protected UpdateRequest(UpdateRequest request) {
    this.origin = request.getOrigin();
    this.properties = request.getProperties();
    this.id = request.getId();
    this.absSender = request.getAbsSender();
    this.chatId = request.getChatId();
    this.messageType = request.getMessageType();
    this.message = request.getMessage();
    this.chat = request.getChat();
    this.user = request.getUser();
    this.text = request.getText();
    this.handlerMethod = request.getHandlerMethod();
    this.templateVariables = request.getTemplateVariables();
    this.basePattern = request.getBasePattern();
    this.state = request.getState();
    this.response = request.getResponse();
    this.objectMapper = request.getObjectMapper();
    this.error = request.getError();
    this.permission = request.getPermission();
  }

  @Override
  public String toString() {
    return "Update = %s".formatted(this.origin);
  }
}
