package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.lang.Nullable;

@BetaApi
public interface Step extends ActionExecutor {

  @Nullable
  String getName();

  @Nullable
  Scenario getRoot();

}
