package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import com.github.drednote.telegram.updatehandler.response.InternalErrorHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.update-handler")
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
   * If at the end of update handling the response is null, set {@link NotHandledHandlerResponse} as
   * response
   */
  private boolean setDefaultAnswer = true;
  /**
   * If exception is occurred and no handler, set {@link InternalErrorHandlerResponse} as response
   */
  private boolean setDefaultErrorAnswer = true;
  /**
   * A time that scenario executor will wait if a concurrent interaction was performed
   */
  private long scenarioLockMs = 5000L;
}
