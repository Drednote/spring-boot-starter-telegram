package io.github.drednote.telegram.updatehandler;

import io.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import io.github.drednote.telegram.updatehandler.response.GenericTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.InternalErrorTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.NotHandledTelegramResponse;
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
   * Enabled {@link MvcUpdateHandler}
   */
  private boolean mvcEnabled = true;
  /**
   * Enabled {@link ScenarioUpdateHandler}
   */
  private boolean scenarioEnabled = true;
  /**
   * If at the end of update handling, the response is null, set {@link NotHandledTelegramResponse}
   * as response
   */
  private boolean setDefaultAnswer = true;
  /**
   * If exception is occurred and no handler, set {@link InternalErrorTelegramResponse} as response
   */
  private boolean setDefaultErrorAnswer = true;
  /**
   * A time that scenario executor will wait if a concurrent interaction was performed
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
   */
  private boolean serializeJavaObjectWithJackson = true;
}
