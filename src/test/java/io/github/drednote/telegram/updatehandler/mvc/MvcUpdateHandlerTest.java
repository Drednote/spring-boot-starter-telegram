package io.github.drednote.telegram.updatehandler.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.CoreAutoConfiguration;
import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.testsupport.UpdateUtils;
import io.github.drednote.telegram.updatehandler.response.TelegramResponse;
import io.github.drednote.telegram.updatehandler.UpdateHandlerAutoConfiguration;
import io.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandlerTest.TestController;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramController;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramRequest;
import io.github.drednote.telegram.updatehandler.response.EmptyTelegramResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@SpringBootTest(classes = {
    UpdateHandlerAutoConfiguration.class, TestController.class, CoreAutoConfiguration.class
})
@Slf4j
@ActiveProfiles("mvcTest")
class MvcUpdateHandlerTest {

  @Autowired
  TestController testController;
  @Autowired
  MvcUpdateHandler updateHandler;

  @Test
  void shouldCallRegister() throws Exception {
    Update update = UpdateUtils.createCommand("/register");
    update.setUpdateId(1);

    updateHandler.onUpdate(new DefaultTelegramUpdateRequest(update, null, null));

    assertThat(testController.registerCount).isEqualTo(1);
  }

  @Test
  void shouldCallText() throws Exception {
    Update update = new Update();
    Message message = new Message();
    message.setText("hello");
    update.setMessage(message);
    update.setUpdateId(1);
    updateHandler.onUpdate(new DefaultTelegramUpdateRequest(update, null, null));

    assertThat(testController.textCount).isEqualTo(1);
  }

  @Slf4j
  @TelegramController
  static class TestController {

    int registerCount = 0;
    int textCount = 0;


    @TelegramRequest(value = "/register", messageType = MessageType.COMMAND)
    public TelegramResponse register(Update update) {
      registerCount++;
      return EmptyTelegramResponse.INSTANCE;
    }

    @TelegramRequest(requestType = RequestType.MESSAGE)
    public void text(
        Update update, Message message, User user
    ) {
      textCount++;
    }
  }
}