package io.github.drednote.telegram.updatehandler.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.testsupport.UpdateUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class GenericHandlerResponseTest {

  @Test
  void shouldCorrectSendString() throws TelegramApiException {
    String text = "1";
    GenericHandlerResponse response = new GenericHandlerResponse(text);
    Update update = UpdateUtils.createMessage("2");
    AbsSender absSender = Mockito.mock(AbsSender.class);

    response.process(new DefaultTelegramUpdateRequest(update, absSender, null));
    Mockito.verify(absSender)
        .execute(new SendMessage(update.getMessage().getChatId().toString(), text));
  }

  @Test
  void shouldCorrectSendBytes() throws TelegramApiException {
    byte[] text = "1".getBytes(StandardCharsets.UTF_8);
    GenericHandlerResponse response = new GenericHandlerResponse(text);
    Update update = UpdateUtils.createMessage("2");
    AbsSender absSender = Mockito.mock(AbsSender.class);

    response.process(new DefaultTelegramUpdateRequest(update, absSender, null));
    Mockito.verify(absSender)
        .execute(new SendMessage(update.getMessage().getChatId().toString(),
            new String(text, StandardCharsets.UTF_8)));
  }

  @Test
  void shouldCorrectSendBotType() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    SendContact sendContact = new SendContact(update.getMessage().getChatId().toString(), "", "");
    GenericHandlerResponse response = new GenericHandlerResponse(sendContact);
    AbsSender absSender = Mockito.mock(AbsSender.class);

    response.process(new DefaultTelegramUpdateRequest(update, absSender, null));
    Mockito.verify(absSender).execute(sendContact);
  }

  @Test
  void shouldCorrectSendMediaType() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    SendAnimation animation = new SendAnimation(update.getMessage().getChatId().toString(),
        new InputFile("1"));
    GenericHandlerResponse response = new GenericHandlerResponse(animation);
    AbsSender absSender = Mockito.mock(AbsSender.class);

    response.process(new DefaultTelegramUpdateRequest(update, absSender, null));
    Mockito.verify(absSender).execute(animation);
  }

  @Test
  void shouldCorrectSendGeneric() throws TelegramApiException {
    Update update = UpdateUtils.createMessage("2");
    DataClass object = new DataClass("1");
    GenericHandlerResponse response = new GenericHandlerResponse(object);
    AbsSender absSender = Mockito.mock(AbsSender.class);

    DefaultTelegramUpdateRequest request = new DefaultTelegramUpdateRequest(update, absSender, null);
    request.setObjectMapper(new ObjectMapper());
    response.process(request);
    Mockito.verify(absSender)
        .execute(new SendMessage(update.getMessage().getChatId().toString(), "text = 1"));
  }

  @JsonSerialize(using = ToStringSerializer.class)
  record DataClass(String text) {

    @Override
    public String toString() {
      return "text = %s".formatted(text);
    }
  }
}

