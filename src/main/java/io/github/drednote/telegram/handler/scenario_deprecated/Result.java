package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.lang.Nullable;

@BetaApi
public interface Result {

  boolean isMade();

  @Nullable
  Object response();
}
