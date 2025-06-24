package io.github.drednote.telegram.session;

import io.github.drednote.telegram.core.DefaultTelegramBot;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.datasource.session.UpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.inmemory.InMemoryUpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import io.github.drednote.telegram.session.processor.OnFlyTelegramUpdateProcessor;
import io.github.drednote.telegram.session.processor.SchedulerTelegramUpdateProcessor;
import io.github.drednote.telegram.session.processor.SchedulerTelegramUpdateProcessorProperties;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.longpolling.interfaces.BackOff;
import org.telegram.telegrambots.longpolling.util.ExponentialBackOff;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram API docs</a>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("drednote.telegram.session")
public class SessionProperties {

    /**
     * LongPolling properties
     */
    @NonNull
    private LongPollingSessionProperties longPolling = new LongPollingSessionProperties();
    /**
     * SchedulerTelegramUpdateProcessor properties.
     */
    @NestedConfigurationProperty
    private SchedulerTelegramUpdateProcessorProperties schedulerProcessor = new SchedulerTelegramUpdateProcessorProperties();
    /**
     * Max number of threads used for consumption messages from a telegram
     */
    @NonNull
    private int consumeMaxThreads = 10;
    /**
     * Limits the number of updates to be store in memory queue for update processing. 0 - no limit. Defaults to
     * (consumeMaxThreads * 1.5).
     */
    @NonNull
    private int maxMessagesInQueue = 15;
    /**
     * Max number of threads used for consumption messages from a telegram for concrete user. 0 - no restrictions.
     */
    @NonNull
    private int maxThreadsPerUser = 1;
    /**
     * Cache lifetime used in {@link OnFlyTelegramUpdateProcessor}. This parameter needed just to delete staled buckets
     * to free up memory
     *
     * @see OnFlyTelegramUpdateProcessor
     */
    @NonNull
    private int cacheLiveDuration = 1;
    /**
     * The {@link TimeUnit} which will be applied to {@link #cacheLiveDuration}
     *
     * @see OnFlyTelegramUpdateProcessor
     */
    @NonNull
    private TimeUnit cacheLiveDurationUnit = TimeUnit.HOURS;
    /**
     * The strategy to receive updates from Telegram API
     *
     * @apiNote type WebHooks not implemented yet
     * @see <a href="https://core.telegram.org/bots/api#getting-updates">Getting updates</a>
     */
    @NonNull
    private UpdateStrategy updateStrategy = UpdateStrategy.LONG_POLLING;
    /**
     * A type of {@link TelegramUpdateProcessor} using.
     */
    private UpdateProcessorType updateProcessorType = UpdateProcessorType.DEFAULT;
    /**
     * Backoff strategy which will be applied if requests to telegram API are failed with errors
     *
     * @apiNote impl of interface {@link BackOff} must have one empty public constructor
     */
    @NonNull
    private Class<? extends BackOff> backOffStrategy = ExponentialBackOff.class;
    /**
     * The proxy type for executing requests to telegram API
     */
    @NonNull
    private ProxyType proxyType = ProxyType.NO_PROXY;
    /**
     * Automatically start session when spring context loaded. If you set this parameter to false, you will be needed to
     * manually call the {@link TelegramBotSession#start()} to start the session and start to consume messages from the
     * Telegram.
     */
    private boolean autoSessionStart = true;

    /**
     * Proxy url in format host:port or if auth needed host:port:username:password.
     */
    @Nullable
    private ProxyUrl proxyUrl;


    public void setProxyUrl(@Nullable String proxyUrl) {
        if (proxyUrl != null) {
            String[] split = proxyUrl.split(":");
            if (split.length == 4) {
                this.proxyUrl = new ProxyUrl(split[0], Integer.parseInt(split[1]), split[2],
                    split[3].toCharArray());
            } else if (split.length == 2) {
                this.proxyUrl = new ProxyUrl(split[0], Integer.parseInt(split[1]));
            } else {
                throw new IllegalArgumentException("Invalid proxy url: " + proxyUrl);
            }
        }
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class ProxyUrl {

        @NonNull
        private final String host;
        private final int port;
        @Nullable
        private String userName;
        @Nullable
        private char[] password;
    }

    @Getter
    @Setter
    public static class LongPollingSessionProperties {

        /**
         * Limits the number of updates to be retrieved. Values between 1-100 are accepted
         */
        @NonNull
        private int updateLimit = 100;
        /**
         * Timeout in seconds for long polling. Should be positive, short polling (0) should be used for testing
         * purposes only
         */
        @NonNull
        private int updateTimeout = 50;
        /**
         * A JSON-serialized list of the update types you want your bot to receive. For example, specify [“message”,
         * “edited_channel_post”, “callback_query”] to only receive updates of these types. See {@link RequestType} for
         * a complete list of available update types. Specify an empty list to receive all update types except
         * chat_member (default). If not specified, the previous setting will be used
         */
        @Nullable
        private List<String> allowedUpdates;
    }

    public enum ProxyType {
        NO_PROXY, HTTP
    }

    public enum UpdateStrategy {
        /**
         * @see DefaultTelegramBot
         * @see LongPollingSession
         */
        LONG_POLLING,
        /**
         * WebHooks not implemented yet
         */
        WEBHOOKS
    }

    public enum UpdateProcessorType {
        /**
         * Using the default processor. Points to {@link UpdateProcessorType#SCHEDULER} instance.
         */
        DEFAULT,

        /**
         * Using {@link SchedulerTelegramUpdateProcessor} instance with {@link InMemoryUpdateInboxRepositoryAdapter} as
         * a persisting adapter.
         * <p>
         * This type is suitable for a small load and not production use due to the specifics of the implementation.
         *
         * @see InMemoryUpdateInboxRepositoryAdapter
         */
        SCHEDULER,

        /**
         * Using {@link SchedulerTelegramUpdateProcessor} instance with any {@link UpdateInboxRepositoryAdapter}, but
         * not {@link InMemoryUpdateInboxRepositoryAdapter} as a persisting adapter.
         * <p>
         * This is the best option for high load or production use since no messages will be lost, and message
         * processing can be parallelized by more than 1 bot if necessary.
         * <p>
         * <b>Note: if you specify this type, you should add any of {@code spring data starter} for datasource and
         * configure it.</b>
         *
         * @see DataSourceAutoConfiguration
         */
        SCHEDULER_WITH_CRUD,

        /**
         * Using {@link OnFlyTelegramUpdateProcessor} instance without any {@link UpdateInboxRepositoryAdapter}.
         * <p>
         * This implementation is different in that it does not store incoming updates in a queue, but immediately tries
         * to process them. This leads to a peculiarity - if one user receives more updates than are allowed to be
         * processed in parallel by user, then all unnecessary updates are thrown out with a
         * {@link TooManyRequestsTelegramResponse}
         */
        ON_FLY
    }
}
