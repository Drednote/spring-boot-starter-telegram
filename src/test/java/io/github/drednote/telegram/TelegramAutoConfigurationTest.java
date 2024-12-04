package io.github.drednote.telegram;

import static org.assertj.core.api.Assertions.assertThatException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

class TelegramAutoConfigurationTest {

    @Nested
    @TestPropertySource(properties = {"drednote.telegram.token="})
    @SpringBootTest(classes = {TelegramProperties.class})
    class NoTokenTest {

        @Autowired
        private TelegramProperties telegramProperties;

        @Test
        void shouldThrowExceptionIfTokenNotSet() {
            assertThatException()
                .isThrownBy(() -> new TelegramAutoConfiguration(telegramProperties))
                .isInstanceOf(BeanCreationException.class);
        }
    }

    @Nested
    @TestPropertySource(properties = {"drednote.telegram.token=", "drednote.telegram.enabled=false"})
    @SpringBootTest(classes = {TelegramAutoConfiguration.class})
    class CheckDisabledBotTest {

        @Autowired
        private ApplicationContext applicationContext;

        @Test
        void shouldThrowExceptionIfTokenNotSet() {
            assertThatException()
                .isThrownBy(() -> applicationContext.getBean(TelegramProperties.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }
}