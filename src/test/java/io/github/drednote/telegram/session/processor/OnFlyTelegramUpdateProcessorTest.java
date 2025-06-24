package io.github.drednote.telegram.session.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
class OnFlyTelegramUpdateProcessorTest {

    OnFlyTelegramUpdateProcessor session;

    TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
    TelegramClient telegramClient;
    private SessionProperties sessionProperties;
    private FilterProperties filterProperties;

    @BeforeEach
    void setUp() {
        telegramClient = Mockito.mock(TelegramClient.class);
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);

        sessionProperties = new SessionProperties();

        sessionProperties.setMaxMessagesInQueue(20);
        sessionProperties.setConsumeMaxThreads(5);
        sessionProperties.setMaxThreadsPerUser(1);
        sessionProperties.setCacheLiveDuration(30);
        sessionProperties.setCacheLiveDurationUnit(TimeUnit.MILLISECONDS);

        filterProperties = new FilterProperties();
        filterProperties.setUserRateLimitUnit(ChronoUnit.MILLIS);
    }

    @RepeatedTest(5)
    @Timeout(value = 2, threadMode = ThreadMode.SEPARATE_THREAD)
    void shouldWaitIfMaxQueueSizeExceed() throws InterruptedException {
        sessionProperties.setCacheLiveDuration(100);
        session = new OnFlyTelegramUpdateProcessor(sessionProperties, filterProperties,
            telegramBot, telegramClient, null);

        List<Update> generate = generate(50);
        doAnswer(answer -> {
            Thread.sleep(10);
            return null;
        }).when(telegramBot).onUpdateReceived(any());

        session.process(generate);

        Thread.sleep(200);

        verify(telegramBot, times(50)).onUpdateReceived(any());
    }

    @Test
    void shouldExecuteUpdateOnlyOneOthersRejected() throws InterruptedException {
        sessionProperties.setCacheLiveDuration(1500);
        session = new OnFlyTelegramUpdateProcessor(sessionProperties, filterProperties,
            telegramBot, telegramClient, null);
        List<Update> generate = generate(5, 1L);
        doAnswer(answer -> {
            Thread.sleep(100);
            return null;
        }).when(telegramBot).onUpdateReceived(any());

        session.process(generate);

        Thread.sleep(200);

        verify(telegramBot, times(1)).onUpdateReceived(any());
    }

    @Test
    void shouldExecuteUpdateOnlyOneOthersRejectedByRateLimit() throws InterruptedException {
        sessionProperties.setCacheLiveDuration(1500);
        filterProperties.setUserRateLimit(2000);
        sessionProperties.setMaxThreadsPerUser(5);
        session = new OnFlyTelegramUpdateProcessor(sessionProperties, filterProperties,
            telegramBot, telegramClient, null);
        List<Update> generate = generate(5, 1L);
        doAnswer(answer -> {
            Thread.sleep(20);
            return null;
        }).when(telegramBot).onUpdateReceived(any());

        session.process(generate);

        Thread.sleep(50);

        verify(telegramBot, times(1)).onUpdateReceived(any());
    }

    private List<Update> generate(int count, long... id) {
        return Stream.iterate(0, i -> i < count, i -> i + 1)
            .map(i -> UpdateBuilder.create().withUpdateId(i).withUser(id.length > 0 ? id[0] : i)
                .message())
            .collect(Collectors.toCollection(ArrayList::new));
    }
}