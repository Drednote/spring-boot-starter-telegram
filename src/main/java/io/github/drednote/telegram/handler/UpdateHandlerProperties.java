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
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
    private boolean controllerEnabled = true;
    /**
     * Enabled scenario update handling
     *
     * @see ScenarioConfigurerAdapter
     * @see ScenarioUpdateHandler
     */
    private boolean scenarioEnabled = true;
    /**
     * If exception is occurred and no handler has processed it, set
     * {@link InternalErrorTelegramResponse} as response
     *
     * @see DefaultExceptionHandler
     */
    private boolean setDefaultErrorAnswer = true;
    /**
     * By default, java pojo objects will be serialized with Jackson to json in
     * {@link GenericTelegramResponse}. Set this parameter to false, if you want to disable this
     * behavior
     *
     * @see GenericTelegramResponse
     */
    private boolean serializeJavaObjectWithJackson = true;
    /**
     * If scenario is enabled and {@link SessionProperties#getMaxThreadsPerUser} is set value other
     * than 1, throws an error with a warning about using scenario safe only when
     * getMaxThreadsPerUser is set to 1.
     */
    private boolean enabledWarningForScenario = true;
}
