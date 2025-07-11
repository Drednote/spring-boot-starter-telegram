package io.github.drednote.telegram.support.builder;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

@RequiredArgsConstructor
public class UpdateBuilder {

    private final Update update = new Update();
    private User user;
    private Chat chat;
    private String text;
    private Integer updateId = 0;

    public static UpdateBuilder create() {
        return new UpdateBuilder();
    }

    public static UpdateBuilder create(Integer updateId) {
        UpdateBuilder updateBuilder = new UpdateBuilder();
        updateBuilder.updateId = updateId;
        return updateBuilder;
    }

    public static UpdateBuilder _default(String text) {
        return create().withChat(1L).withUser(2L).withText(text);
    }

    public UpdateBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public UpdateBuilder withUser(Long id) {
        User user = new User(id, "first_name", false);
        return withUser(user);
    }

    public UpdateBuilder withChat(Chat chat) {
        this.chat = chat;
        return this;
    }

    public UpdateBuilder withChat(Long id) {
        Chat chat = new Chat(id, "private");
        return withChat(chat);
    }

    public UpdateBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public UpdateBuilder withUpdateId(Integer updateId) {
        this.updateId = updateId;
        return this;
    }

    public Update message() {
        buildDefault();

        Message message = new Message();
        message.setChat(chat);
        message.setFrom(user);
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    public Update inlineQuery() {
        buildDefault();

        InlineQuery inlineQuery = new InlineQuery("id", user, text, "0");
        update.setInlineQuery(inlineQuery);
        return update;
    }

    public Update chosenInlineQuery() {
        buildDefault();

        ChosenInlineQuery chosenInlineQuery = new ChosenInlineQuery("id", user, text);
        update.setChosenInlineQuery(chosenInlineQuery);
        return update;
    }

    public Update callbackQuery() {
        buildDefault();

        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setFrom(user);
        callbackQuery.setData(text);
        update.setCallbackQuery(callbackQuery);
        return update;
    }

    public Update shippingQuery() {
        buildDefault();

        ShippingQuery shippingQuery = new ShippingQuery();
        shippingQuery.setFrom(user);
        shippingQuery.setInvoicePayload(text);
        update.setShippingQuery(shippingQuery);
        return update;
    }

    public Update preCheckoutQuery() {
        buildDefault();

        PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery();
        preCheckoutQuery.setFrom(user);
        preCheckoutQuery.setInvoicePayload(text);
        update.setPreCheckoutQuery(preCheckoutQuery);
        return update;
    }

    public Update poll() {
        buildDefault();

        Poll poll = new Poll();
        poll.setQuestion(text);
        update.setPoll(poll);
        return update;
    }

    public Update pollAnswer() {
        buildDefault();

        PollAnswer pollAnswer = new PollAnswer();
        pollAnswer.setUser(user);
        update.setPollAnswer(pollAnswer);
        return update;
    }

    public Update chatMemberUpdated() {
        buildDefault();

        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setFrom(user);
        chatMemberUpdated.setChat(chat);
        update.setChatMember(chatMemberUpdated);
        return update;
    }

    public Update chatJoinRequest() {
        buildDefault();

        ChatJoinRequest chatJoinRequest = new ChatJoinRequest();
        chatJoinRequest.setUser(user);
        chatJoinRequest.setChat(chat);
        update.setChatJoinRequest(chatJoinRequest);
        return update;
    }

    public Update command() {
        buildDefault();

        Message message = new Message();
        message.setChat(chat);
        message.setFrom(user);
        message.setText(text);
        message.setEntities(List.of(new MessageEntity(EntityType.BOTCOMMAND, 0, text.length())));
        update.setMessage(message);
        return update;
    }

    private void buildDefault() {
        update.setUpdateId(updateId);
    }
}
