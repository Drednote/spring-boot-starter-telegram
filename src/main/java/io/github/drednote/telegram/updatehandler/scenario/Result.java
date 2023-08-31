package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.lang.Nullable;

@BetaApi
public interface Result {

  boolean isMade();

  @Nullable
  Object response();
}
