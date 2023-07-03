package com.github.drednote.telegram.core;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

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
import org.telegram.telegrambots.meta.generics.TelegramBot;

@Getter
public sealed class UpdateRequest permits ImmutableUpdateRequest {

  private final Update origin;
  private final TelegramBot bot;
  private final Long chatId;
  private final RequestType messageType;

  @Nullable
  private final Message message;
  @Nullable
  private final Chat chat;
  @Nullable
  private final User user;
  @Nullable
  private final String text;

  // mvc
  @Setter
  @Nullable
  private HandlerMethod handlerMethod;
  @Setter
  @Nullable
  private Map<String, String> templateVariables;
  @Setter
  @Nullable
  private String basePattern;

  // scenario
  @Setter
  private Object state;

  // response
  @Setter
  private HandlerResponse response;

  public UpdateRequest(@NonNull Update update, TelegramBot bot) {
    this.origin = update;
    this.bot = bot;

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
    } else {
//      InlineQuery inlineQuery = update.getInlineQuery();
//      if (inlineQuery != null) {
//        this.user = inlineQuery.getFrom();
//        this.text = inlineQuery.getQuery();
//        this.chat = null;
//        this.messageType = RequestType.INLINE_QUERY;
//      } else {
//        this.chat = null;
//        ChosenInlineQuery chosenInlineResult = update.getChosenInlineQuery();
//        if (chosenInlineResult != null) {
//          this.user = chosenInlineResult.getFrom();
//          this.text = chosenInlineResult.getQuery();
//          this.messageType = RequestType.INLINE_CHOSEN;
//        } else {
//          CallbackQuery callbackQuery = update.getCallbackQuery();
//          if (callbackQuery != null) {
//            this.user = callbackQuery.getFrom();
//            this.text = callbackQuery.getData();
//            this.messageType = RequestType.INLINE_CALLBACK;
//          } else {
      this.messageType = RequestType.MESSAGE;
      this.user = null;
      this.text = null;
      this.chat = null;
    }
//        }
//      }
//    }
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
    this.bot = request.getBot();
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
  }
}
