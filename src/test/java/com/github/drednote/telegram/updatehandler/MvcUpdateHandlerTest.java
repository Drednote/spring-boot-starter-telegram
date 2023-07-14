package com.github.drednote.telegram.updatehandler;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.mvc.BotControllerContainer;
import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

@SpringBootTest(classes = {
    UpdateHandlerAutoConfiguration.class, TestController.class
})
@Slf4j
@ActiveProfiles("mvcTest")
class MvcUpdateHandlerTest {

  @Autowired
  BotControllerContainer container;
  @Autowired
  TestController testController;
  UpdateHandler updateHandler;

  @BeforeEach
  void setUp() {
    updateHandler = new MvcUpdateHandler(container);
  }

  @Test
  void shouldCallRegister() throws Exception {
    Update update = new Update();
    Message message = new Message();
    message.setText("/register");
    message.setEntities(List.of(new MessageEntity(EntityType.BOTCOMMAND, 0, "/register".length())));
    update.setMessage(message);
    update.setUpdateId(1);

    updateHandler.onUpdate(new UpdateRequest(update, null, null));

    assertThat(testController.getRegisterCount()).isEqualTo(1);
  }

  @Test
  void shouldCallText() throws Exception {
    Update update = new Update();
    Message message = new Message();
    message.setText("hello");
    update.setMessage(message);
    update.setUpdateId(1);
    updateHandler.onUpdate(new UpdateRequest(update, null, null));

    assertThat(testController.getTextCount()).isEqualTo(1);
  }
}