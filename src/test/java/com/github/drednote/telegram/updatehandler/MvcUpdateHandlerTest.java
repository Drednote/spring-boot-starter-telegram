package com.github.drednote.telegram.updatehandler;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@SpringBootTest(classes = {
    UpdateHandlerAutoConfiguration.class, TestController.class
})
@Slf4j
@ActiveProfiles("mvcTest")
class MvcUpdateHandlerTest {

  @Autowired
  HandlerMethodContainer container;
  @Autowired
  TestController testController;
  UpdateHandler updateHandler;

  @BeforeEach
  void setUp() {
    updateHandler = new MvcUpdateHandler(container);
  }

  @Test
  void shouldCallRegister() {
    Update update = new Update();
    Message message = new Message();
    message.setText("/register");
    update.setMessage(message);

    UpdateHandlerResponse response = updateHandler.onUpdate(update);

    assertThat(testController.getRegisterCount()).isEqualTo(1);
  }

  @Test
  void shouldCallText() {
    Update update = new Update();
    Message message = new Message();
    message.setText("hello");
    update.setMessage(message);
    UpdateHandlerResponse response = updateHandler.onUpdate(update);

    assertThat(testController.getTextCount()).isEqualTo(1);
  }
}