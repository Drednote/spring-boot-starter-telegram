package io.github.drednote.telegram.session;

import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.session.SessionProperties.ProxyType;
import io.github.drednote.telegram.session.SessionProperties.ProxyUrl;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.BackOff;
import org.telegram.telegrambots.meta.TelegramUrl;

/**
 * Autoconfiguration class for managing Telegram bot sessions and scopes.
 *
 * <p>This class provides automatic configuration for different types of Telegram bot sessions,
 * including long polling and webhooks, based on properties defined in the application's
 * configuration.
 */
@AutoConfiguration
@EnableConfigurationProperties(SessionProperties.class)
public class SessionAutoConfiguration {

    /**
     * Configures a bean for the Telegram bot session using long polling. And starts session
     *
     * @param telegramClient The Telegram client used to interact with the Telegram API
     * @param properties     Configuration properties for the session
     * @return The configured Telegram bot session
     */
    @Bean(destroyMethod = "stop")
    @ConditionalOnProperty(
        prefix = "drednote.telegram.session",
        name = "type",
        havingValue = "LONG_POLLING",
        matchIfMissing = true
    )
    @ConditionalOnMissingBean
    public TelegramBotSession longPollingTelegramBotSession(
        TelegramClient telegramClient, SessionProperties properties,
        TelegramProperties telegramProperties, TelegramUpdateProcessor processor
    ) {
        try {
            Class<? extends BackOff> backOffClazz = properties.getBackOffStrategy();
            BackOff backOff = backOffClazz.getDeclaredConstructor().newInstance();
            return new LongPollingSession(
                telegramClient, properties, telegramProperties, backOff, processor);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new BeanCreationException("Cannot initiate BackOff", e);
        }
    }

    /**
     * Configures a bean for the Telegram bot session using webhooks.
     *
     * <p><b>Throws {@link UnsupportedOperationException} because webhooks are not yet
     * implemented</b>
     *
     * @return The configured Telegram bot session
     */
    @Bean(destroyMethod = "stop")
    @ConditionalOnProperty(
        prefix = "drednote.telegram.session",
        name = "type",
        havingValue = "WEBHOOKS"
    )
    @ConditionalOnMissingBean
    public TelegramBotSession webhooksTelegramBotSession() {
        throw new UnsupportedOperationException("Webhooks not implemented yet");
    }

    @EventListener(value = ApplicationReadyEvent.class)
    public void onStartUp(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        SessionProperties properties = context.getBean(SessionProperties.class);
        if (properties.isAutoSessionStart()) {
            TelegramBotSession session = context.getBean(TelegramBotSession.class);
            session.start();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(TelegramBot.class)
    public TelegramUpdateProcessor telegramUpdateProcessor(
        SessionProperties properties, FilterProperties filterProperties, TelegramBot telegramBot,
        org.telegram.telegrambots.meta.generics.TelegramClient telegramClient,
        TelegramMessageSource messageSource
    ) {
        return new DefaultTelegramUpdateProcessor(
            properties, filterProperties, telegramBot, telegramClient, messageSource);
    }

    /**
     * Configures a bean for the Telegram client to interact with the Telegram API.
     *
     * @return The configured Telegram client
     */
    @Bean
    @ConditionalOnMissingBean
    public TelegramClient telegramClient(SessionProperties properties) {
        return getFactory(builder -> {
            if (properties.getProxyType() == ProxyType.HTTP) {
                throwIfProxyNull(properties);
                builder.requestFactory(configureProxy(properties.getProxyUrl()));
            }
        }).createClient(TelegramClient.class);
    }

    private static HttpServiceProxyFactory getFactory(Consumer<Builder> additionalSettings) {
        RestClient.Builder builder = RestClient.builder();

        additionalSettings.accept(builder);

        RestClient restClient = builder
            .baseUrl(
                TelegramUrl.DEFAULT_URL.getSchema() + "://" + TelegramUrl.DEFAULT_URL.getHost())
            .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    private static HttpComponentsClientHttpRequestFactory configureProxy(ProxyUrl proxyUrl) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        HttpHost myProxy = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort());
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        clientBuilder.setProxy(myProxy).disableCookieManagement();

        if (proxyUrl.getUserName() != null) {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                new AuthScope(myProxy),
                new UsernamePasswordCredentials(proxyUrl.getUserName(), proxyUrl.getPassword())
            );
            clientBuilder.setDefaultCredentialsProvider(credsProvider);
        }

        HttpClient httpClient = clientBuilder.build();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public org.telegram.telegrambots.meta.generics.TelegramClient absSender(
        SessionProperties properties, TelegramProperties telegramProperties) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        if (properties.getProxyType() == ProxyType.HTTP) {
            ProxyUrl proxyUrl = properties.getProxyUrl();
            throwIfProxyNull(properties);
            okHttpClient.proxy(new Proxy(Type.HTTP,
                new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort())));
        }
        OkHttpClient httpClient = okHttpClient.build();
        return new OkHttpTelegramClient(httpClient, telegramProperties.getToken());
    }

    private static void throwIfProxyNull(SessionProperties properties) {
        if (properties.getProxyUrl() == null) {
            throw new IllegalArgumentException("If proxy is enabled, proxy url is required");
        }
    }
}
