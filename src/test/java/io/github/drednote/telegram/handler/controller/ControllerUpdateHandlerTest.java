package io.github.drednote.telegram.handler.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.CoreAutoConfiguration;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.handler.UpdateHandlerAutoConfiguration;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandlerTest.TestController;
import io.github.drednote.telegram.response.EmptyTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
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
class ControllerUpdateHandlerTest {

    @Autowired
    TestController testController;
    @Autowired
    ControllerUpdateHandler updateHandler;

    @Autowired
    HandlerMethodPopular popular;

    @Test
    void shouldCallRegister() throws Exception {
        Update update = UpdateUtils.createCommand("/register");
        update.setUpdateId(1);

        DefaultUpdateRequest mockRequest = UpdateRequestUtils.createMockRequest(update);
        popular.populate(mockRequest);
        updateHandler.onUpdate(mockRequest);

        assertThat(testController.registerCount).isEqualTo(1);
    }

    @Test
    void shouldCallText() throws Exception {
        Update update = new Update();
        Message message = new Message();
        message.setText("hello");
        update.setMessage(message);
        update.setUpdateId(1);
        DefaultUpdateRequest mockRequest = UpdateRequestUtils.createMockRequest(update);
        popular.populate(mockRequest);
        updateHandler.onUpdate(mockRequest);

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