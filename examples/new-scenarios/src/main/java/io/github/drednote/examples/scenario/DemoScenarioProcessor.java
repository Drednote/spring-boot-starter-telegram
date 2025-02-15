package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DemoScenarioProcessor {
    private final WebClient webClient;
    private final String oldPass = "1234";

    public DemoScenarioProcessor(WebClient webClient) {
        this.webClient = webClient;
    }


    SendMessage processChangePassword(UserScenarioContext context) {
        // First enter
        if(!context.getData().has("passTimes")) {
            context.getData().put("passTimes", 2);
            return SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Enter new password:").build();
        }else{
            if (Objects.equals(context.getUpdateRequest().getText(), oldPass)) {
                context.getData().remove("passTimes");
                context.getData().put("passNotWrong", true);
                return null;
            } else {
                context.getData().put("passNotWrong", true);
                context.getData().put("passTimes", (int) context.getData().get("passTimes") - 1);
                return SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Wrong pass try again:").build();
            }
        }


    }

    SendMessage sendFirstMenu(UserScenarioContext context) {
        SendMessage message = SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Choose an option:").build();

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createButtonRow("Weather", "weather"));
        keyboard.add(createButtonRow("Change password", "change_password"));
        keyboard.add(createButtonRow("To sub scenario", "to_sub_scenario"));

        message.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build());
        return message;
    }

    SendMessage showWeather(UserScenarioContext context) {
        try {
            context.getUpdateRequest().getAbsSender().execute(DeleteMessage.builder().chatId(context.getUpdateRequest().getChatId()).messageId(context.getUpdateRequest().getOrigin().getCallbackQuery().getMessage().getMessageId()).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return SendMessage.builder().chatId(context.getUpdateRequest().getChatId()).text("Frankfurt's weather: " + Objects.requireNonNull(getWeatherInFrankfurt().block())).build();
    }

    private InlineKeyboardRow createButtonRow(String text, String callbackData) {
        return new InlineKeyboardRow(InlineKeyboardButton.builder().text(text).callbackData(callbackData).build());
    }

    public Mono<String> getWeatherInFrankfurt() {
        String url = "https://wttr.in/Frankfurt?format=%C+%t";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }
}
