package com.github.drednote.telegram.updatehandler.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.core.request.MessageType;
import com.github.drednote.telegram.testsupport.UpdateUtils;
import com.github.drednote.telegram.core.request.RequestType;
import com.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.UpdateHandlerAutoConfiguration;
import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandlerTest.TestController;
import com.github.drednote.telegram.updatehandler.mvc.annotation.TelegramController;
import com.github.drednote.telegram.updatehandler.mvc.annotation.TelegramRequest;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@SpringBootTest(classes = {
    UpdateHandlerAutoConfiguration.class, TestController.class
})
@Slf4j
@ActiveProfiles("mvcTest")
class MvcUpdateHandlerTest {

  @Autowired
  TelegramControllerContainer container;
  @Autowired
  TestController testController;
  UpdateHandler updateHandler;

  @BeforeEach
  void setUp() {
    updateHandler = new MvcUpdateHandler(container);
  }

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
    public HandlerResponse register(Update update) {
      registerCount++;
      return EmptyHandlerResponse.INSTANCE;
    }

    @TelegramRequest(requestType = RequestType.MESSAGE)
    public void text(
        Update update, Message message, User user
    ) {
      textCount++;
    }
  }
}