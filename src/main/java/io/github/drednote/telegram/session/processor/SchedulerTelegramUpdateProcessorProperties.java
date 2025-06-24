package io.github.drednote.telegram.session.processor;

import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.session.SessionProperties.UpdateProcessorType;
import io.github.drednote.telegram.session.TelegramBotSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
@Getter
@Setter
public class SchedulerTelegramUpdateProcessorProperties {

    /**
     * Automatically start {@link SchedulerTelegramUpdateProcessor} {@link TelegramBotSession#start()} is called. If you
     * set this parameter to false, you will be needed to manually call the
     * {@link SchedulerTelegramUpdateProcessor#start()} to start the session and start to process messages from the
     * Telegram.
     */
    @NonNull
    private boolean autoSessionStart = true;

    /**
     * Maximum interval after which to check for new messages for processing. In milliseconds.
     */
    @NonNull
    private int maxInterval = 1000;

    /**
     * Minimum interval after which to check for new messages for processing. In milliseconds.
     */
    @NonNull
    private int minInterval = 0;

    /**
     * How much to decrease the interval if messages are found for processing. In milliseconds.
     */
    @NonNull
    private int reducingIntervalAmount = 500;

    /**
     * How much to increase the interval if no message is found for processing. In milliseconds.
     */
    @NonNull
    private int increasingIntervalAmount = 100;

    /**
     * Interval after which to check for new messages for processing while all threads are busy. In milliseconds.
     */
    @NonNull
    private int waitInterval = 30;

    /**
     * Interval after tasks is marked {@link UpdateInboxStatus#TIMEOUT}. In milliseconds.
     */
    @NonNull
    private int idleInterval = 30000;

    /**
     * Interval to check that tasks is idle. In milliseconds.
     */
    @NonNull
    private int checkIdleInterval = 5000;

    /**
     * Limits the number of updates to be store in memory queue for update processing for concrete user. 0 - no
     * restrictions.
     * <p>
     * Applied only for {@link UpdateProcessorType#SCHEDULER}
     */
    @NonNull
    private int maxMessageInQueuePerUser = 0;
}
