package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.BotRequest;
import org.springframework.lang.Nullable;

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
   * @apiNote If you call {@link Scenario#makeStep(BotRequest)}, a result of this method will be
   * changed
   */
  Step getCurrentStep();

  /**
   * Search for the next step and if it exists, make the step
   *
   * @return result of performed action
   */
  Result makeStep(BotRequest updateRequest) throws ScenarioException;

  /**
   * @return true if a scenario has no more steps, false if a step can be isMade
   */
  boolean isFinished();
}
