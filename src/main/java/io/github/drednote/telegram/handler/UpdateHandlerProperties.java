package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.exception.DefaultExceptionHandler;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.InternalErrorTelegramResponse;
import io.github.drednote.telegram.session.SessionProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Configuration
@ConfigurationProperties("drednote.telegram.update-handler")
@Getter
@Setter
public class UpdateHandlerProperties {

    /**
     * Enabled controller update handling
     *
     * @see TelegramController
     * @see TelegramRequest
     * @see ControllerUpdateHandler
     */
    @NonNull
    private boolean controllerEnabled = true;
    /**
     * Enabled scenario update handling
     *
     * @see ScenarioConfigurerAdapter
     * @see ScenarioUpdateHandler
     */
    @NonNull
    private boolean scenarioEnabled = true;
    /**
     * If exception is occurred and no handler has processed it, set {@link InternalErrorTelegramResponse} as response
     *
     * @see DefaultExceptionHandler
     */
    @NonNull
    private boolean setDefaultErrorAnswer = true;
    /**
     * By default, java pojo objects will be serialized with Jackson to json in {@link GenericTelegramResponse}. Set
     * this parameter to false, if you want to disable this behavior
     *
     * @see GenericTelegramResponse
     */
    @NonNull
    private boolean serializeJavaObjectWithJackson = true;
    /**
     * Default parse mode of a text message sent to telegram. Applies only if you return raw string from update
     * processing ({@link UpdateHandler})
     */
    @NonNull
    private ParseMode parseMode = ParseMode.NO;
    /**
     * If scenario is enabled and {@link SessionProperties#getMaxThreadsPerUser} is set value other than 1, throws an
     * error with a warning about using scenario safe only when getMaxThreadsPerUser is set to 1.
     */
    @NonNull
    private boolean enabledWarningForScenario = true;

    @RequiredArgsConstructor
    @Getter
    public enum ParseMode {
        NO(null),
        MARKDOWN("Markdown"),
        MARKDOWN_V2("MarkdownV2"),
        HTML("html");

        @Nullable
        private final String value;
    }
}
