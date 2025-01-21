package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@RequiredArgsConstructor
@TelegramScenario
public class ScenarioFactory {

    @TelegramScenarioAction(fullName = true)
    public SendMessage initialTest(ActionContext<?> context) {
        InlineKeyboardRow keyboardRow = new InlineKeyboardRow();
        keyboardRow.add(getButton("1"));
        keyboardRow.add(getButton("2"));

        return SendMessage.builder()
            .chatId(context.getUpdateRequest().getChatId())
            .text("Choose number:")
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build())
            .build();
    }

    @TelegramScenarioAction
    public EditMessageText choose(ActionContext<?> context) {
        UpdateRequest request = context.getUpdateRequest();
        Integer messageId = getMessageId(request);

        InlineKeyboardRow keyboardRow = new InlineKeyboardRow();
        keyboardRow.add(InlineKeyboardButton.builder()
            .text("Print")
            .callbackData("print")
            .build());

        return EditMessageText.builder()
            .chatId(request.getChatId())
            .messageId(messageId)
            .text("Number: " + context.getTemplateVariables().get("value"))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .keyboardRow(backRow())
                .build())
            .build();
    }

    @TelegramScenarioAction
    public String print(ActionContext<?> context) {
        UpdateRequest request = context.getUpdateRequest();
        CallbackQuery callbackQuery = request.getOrigin().getCallbackQuery();

        return Optional.ofNullable(callbackQuery)
            .map(CallbackQuery::getMessage)
            .filter(val -> val instanceof Message)
            .map(mes -> ((Message) mes).getText())
            .orElseThrow(() -> new IllegalStateException("Message not found"));
    }

    private static Integer getMessageId(UpdateRequest request) {
        CallbackQuery callbackQuery = request.getOrigin().getCallbackQuery();
        return Optional.ofNullable(callbackQuery)
            .map(CallbackQuery::getMessage)
            .map(MaybeInaccessibleMessage::getMessageId)
            .orElseThrow(() -> new IllegalStateException("Message ID not found"));
    }

    @TelegramScenarioAction("scenarioBack")
    public EditMessageText scenarioBack(ActionContext<?> context) {
        UpdateRequest request = context.getUpdateRequest();
        Integer messageId = getMessageId(request);
        InlineKeyboardRow keyboardRow = new InlineKeyboardRow();
        keyboardRow.add(getButton("1"));
        keyboardRow.add(getButton("2"));

        return EditMessageText.builder()
            .chatId(context.getUpdateRequest().getChatId())
            .messageId(messageId)
            .text("Choose number:")
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build())
            .build();
    }

    private InlineKeyboardRow backRow() {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
            .text("<< Back")
            .callbackData("back")
            .build());
        return row;
    }

    private static InlineKeyboardButton getButton(String id) {
        return InlineKeyboardButton.builder()
            .text(id)
            .callbackData("choose-" + id)
            .build();
    }
}
