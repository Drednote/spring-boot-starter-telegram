package io.github.drednote.telegram.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
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
    TelegramClient absSender = request.getAbsSender();

    response.process(request);
    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), text);
    sendMessage.setParseMode(null);
    Mockito.verify(absSender).execute(sendMessage);
  }

  @Test
  void shouldCorrectSendBytes() throws TelegramApiException {
    byte[] text = "1".getBytes(StandardCharsets.UTF_8);
    GenericTelegramResponse response = new GenericTelegramResponse(text);
    Update update = UpdateUtils.createMessage("2");

    var request = UpdateRequestUtils.createMockRequest(update);
    TelegramClient absSender = request.getAbsSender();

    response.process(request);
    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
        new String(text, StandardCharsets.UTF_8));
    sendMessage.setParseMode(null);
    Mockito.verify(absSender).execute(sendMessage);
  }

  @Test
  void shouldCorrectSendBotType() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    SendContact sendContact = new SendContact(update.getMessage().getChatId().toString(), "", "");
    GenericTelegramResponse response = new GenericTelegramResponse(sendContact);

    var request = UpdateRequestUtils.createMockRequest(update);
    TelegramClient absSender = request.getAbsSender();

    response.process(request);
    Mockito.verify(absSender).execute(sendContact);
  }

  @Test
  void shouldCorrectSendMediaType() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    SendAnimation animation = new SendAnimation(update.getMessage().getChatId().toString(),
        new InputFile("1"));
    GenericTelegramResponse response = new GenericTelegramResponse(animation);
    var request = UpdateRequestUtils.createMockRequest(update);
    TelegramClient absSender = request.getAbsSender();

    response.process(request);
    Mockito.verify(absSender).execute(animation);
  }

  @Test
  void shouldCorrectSendGeneric() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    DataClass object = new DataClass("1");
    GenericTelegramResponse response = new GenericTelegramResponse(object);

    var request = UpdateRequestUtils.createMockRequest(update);
    TelegramClient absSender = request.getAbsSender();

    response.process(request);
    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "text = 1");
    sendMessage.setParseMode(null);
    Mockito.verify(absSender).execute(sendMessage);
  }

  @JsonSerialize(using = ToStringSerializer.class)
  record DataClass(String text) {

    @Override
    public String toString() {
      return "text = %s".formatted(text);
    }
  }
}

