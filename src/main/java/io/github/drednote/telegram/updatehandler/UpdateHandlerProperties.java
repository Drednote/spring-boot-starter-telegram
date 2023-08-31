package io.github.drednote.telegram.updatehandler;

import io.github.drednote.telegram.exception.DefaultExceptionHandler;
import io.github.drednote.telegram.updatehandler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.updatehandler.controller.TelegramController;
import io.github.drednote.telegram.updatehandler.controller.TelegramRequest;
import io.github.drednote.telegram.updatehandler.response.GenericTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.InternalErrorTelegramResponse;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioAdapter;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
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
   * @see ScenarioAdapter
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
   * A time that scenario executor will wait if a concurrent interaction was performed. 0 - no
   * limit
   */
  private long scenarioLockMs = 0L;
  /**
   * If it needs to autoconfigure scenarioPersister if no one provided
   */
  private boolean autoConfigureScenarioPersister = true;
  /**
   * By default, java pojo objects will be serialized with Jackson to json in
   * {@link GenericTelegramResponse}. Set this parameter to false, if you want to disable this
   * behavior
   *
   * @see GenericTelegramResponse
   */
  private boolean serializeJavaObjectWithJackson = true;
}
