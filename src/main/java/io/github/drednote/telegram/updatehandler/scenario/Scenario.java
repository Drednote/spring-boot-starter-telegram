package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.lang.Nullable;

@BetaApi
public sealed interface Scenario permits ScenarioImpl {

  /**
   * ID of a scenario, commonly id of user
   */
  Long getId();

  /**
   * Unique name of Scenario
   *
   * @return null if no scenario initiated
   */
  @Nullable
  String getName();

  /**
   * @return current step or {@link EmptyStep#INSTANCE} if no step exists for the scenario
   * @apiNote If you call {@link Scenario#makeStep(TelegramUpdateRequest)}, a result of this method will be
   * changed
   */
  Step getCurrentStep();

  /**
   * Search for the next step and if it exists, make the step
   *
   * @return result of performed action
   */
  Result makeStep(TelegramUpdateRequest updateRequest) throws ScenarioException;

  /**
   * @return true if a scenario has no more steps, false if a step can be isMade
   */
  boolean isFinished();
}
