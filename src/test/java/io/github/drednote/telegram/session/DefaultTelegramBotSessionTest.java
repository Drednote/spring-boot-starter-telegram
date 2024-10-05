package io.github.drednote.telegram.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.session.SessionProperties.LongPollingSessionProperties;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

class DefaultTelegramBotSessionTest {

    DefaultTelegramBotSession session;

    TelegramBot telegramBot = Mockito.mock(TelegramBot.class);

    @BeforeEach
    void setUp() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);

        SessionProperties sessionProperties = new SessionProperties();
        LongPollingSessionProperties longPolling = new LongPollingSessionProperties();
        sessionProperties.setLongPolling(longPolling);

        longPolling.setMaxMessagesInQueue(20);
        sessionProperties.setConsumeMaxThreads(5);
        sessionProperties.setMaxThreadsPerUser(1);
        sessionProperties.setCacheLiveDuration(30);
        sessionProperties.setCacheLiveDurationUnit(TimeUnit.MILLISECONDS);

        session = new DefaultTelegramBotSession(sessionProperties, telegramBot) {
            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }
        };
    }

    @Test
    void shouldWaitIfMaxQueueSizeExceed() throws InterruptedException {
        List<Update> generate = generate(50);
        doAnswer(answer -> {
            Thread.sleep(10);
            return null;
        }).when(telegramBot).onUpdateReceived(any());

        session.processUpdates(generate);

        for (int i = 0; i < 70; i++) {
            Thread.sleep(10);
            assertThat(session.updatesCount).hasValueLessThan(21);
        }

        verify(telegramBot, times(50)).onUpdateReceived(any());
    }

    @Test
    void shouldExecuteUpdateSequentially() throws InterruptedException {
        List<LocalDateTime> dateTimes = new ArrayList<>();
        List<Update> generate = generate(5, 1L);
        doAnswer(answer -> {
            Thread.sleep(10);
            dateTimes.add(LocalDateTime.now());
            return null;
        }).when(telegramBot).onUpdateReceived(any());

        session.processUpdates(generate);

        Thread.sleep(300);

        verify(telegramBot, times(5)).onUpdateReceived(any());
        assertThat(dateTimes).hasSize(5);
        for (int i = 0; i < dateTimes.size() - 1; i++) {
            LocalDateTime dateTime = dateTimes.get(i);
            LocalDateTime dateTimeNext = dateTimes.get(i + 1);
            assertThat(Duration.between(dateTime, dateTimeNext)).isGreaterThan(Duration.ofMillis(10));
        }
    }

    private List<Update> generate(int count, long... id) {
        return Stream.iterate(0, i -> i < count, i -> i + 1)
            .map(i -> UpdateBuilder.create().withUpdateId(i).withUser(id.length > 0 ? id[0] : i).message())
            .collect(Collectors.toCollection(ArrayList::new));
    }
}