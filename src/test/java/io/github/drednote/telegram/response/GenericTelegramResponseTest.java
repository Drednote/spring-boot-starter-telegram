package io.github.drednote.telegram.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class GenericTelegramResponseTest {

    @Test
    void shouldCorrectSendString() throws TelegramApiException {
        String text = "1";
        GenericTelegramResponse response = new GenericTelegramResponse(text);
        Update update = UpdateUtils.createMessage("2");

        var request = UpdateRequestUtils.createMockRequest(update);
        TelegramClient absSender = request.getTelegramClient();

        response.process(request);
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), text);
        sendMessage.setParseMode(null);
        verify(absSender).execute(sendMessage);
    }

    @Test
    void shouldCorrectSendBytes() throws TelegramApiException {
        byte[] text = "1".getBytes(StandardCharsets.UTF_8);
        GenericTelegramResponse response = new GenericTelegramResponse(text);
        Update update = UpdateUtils.createMessage("2");

        var request = UpdateRequestUtils.createMockRequest(update);
        TelegramClient absSender = request.getTelegramClient();

        response.process(request);
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
            new String(text, StandardCharsets.UTF_8));
        sendMessage.setParseMode(null);
        verify(absSender).execute(sendMessage);
    }

    @Test
    void shouldCorrectSendBotType() throws TelegramApiException {
        Update update = UpdateUtils.createMessage("2");
        SendContact sendContact = new SendContact(update.getMessage().getChatId().toString(), "", "");
        GenericTelegramResponse response = new GenericTelegramResponse(sendContact);

        var request = UpdateRequestUtils.createMockRequest(update);
        TelegramClient absSender = request.getTelegramClient();

        response.process(request);
        verify(absSender).execute(sendContact);
    }

    @Test
    void shouldCorrectSendMediaType() throws TelegramApiException {
        Update update = UpdateUtils.createMessage("2");
        SendAnimation animation = new SendAnimation(update.getMessage().getChatId().toString(),
            new InputFile("1"));
        GenericTelegramResponse response = new GenericTelegramResponse(animation);
        var request = UpdateRequestUtils.createMockRequest(update);
        TelegramClient absSender = request.getTelegramClient();

        response.process(request);
        verify(absSender).execute(animation);
    }

    @Test
    void shouldCorrectSendGeneric() throws TelegramApiException {
        Update update = UpdateUtils.createMessage("2");
        DataClass object = new DataClass("1");
        GenericTelegramResponse response = new GenericTelegramResponse(object);
        response.setObjectMapper(new ObjectMapper());
        response.setTelegramProperties(new TelegramProperties());

        var request = UpdateRequestUtils.createMockRequest(update);
        TelegramClient absSender = request.getTelegramClient();

        response.process(request);
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "text = 1");
        sendMessage.setParseMode(null);
        verify(absSender).execute(sendMessage);
    }

    @Test
    void shouldCorrectRecognizeChildren() {
        assertThat(new GenericTelegramResponse(Mockito.mock(SendMediaGroup.class))
            .isSendBotApiMethod(Mockito.mock(TelegramClient.class))).isTrue();
        assertThat(new GenericTelegramResponse(Mockito.mock(SendAudio.class))
            .isSendBotApiMethod(Mockito.mock(TelegramClient.class))).isTrue();
        assertThat(new GenericTelegramResponse(Mockito.mock(SendMessage.class))
            .isSendBotApiMethod(Mockito.mock(TelegramClient.class))).isFalse();
    }

    @JsonSerialize(using = ToStringSerializer.class)
    record DataClass(String text) {

        @Override
        public String toString() {
            return "text = %s".formatted(text);
        }
    }
}

