package com.github.drednote.telegram.updatehandler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.update-handler")
@Data
public class UpdateHandlerProperties {

  private Type type = Type.LOGGING;

  public enum Type {
    MVC, STATE_MACHINE, SCENARIO,
    /**
     * just mock type, used until not changed. Can be used for debug
     */
    LOGGING
  }
}
