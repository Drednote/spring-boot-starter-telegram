package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;

/**
 * Interface for configuring rollback transitions in scenarios.
 * <p>
 * During this configurer will be created a new one transition with a reverse direction. For
 * example, you create A → B transition, there will be created B → A transition with personal
 * telegramRequest matching and action (you should specify it).
 *
 * @param <S> the type of the scenario
 * @author Ivan Galushko
 */
public interface ScenarioRollbackTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioRollbackTransitionConfigurer<S>, S> {

    /**
     * Sets the action to be performed during the rollback.
     *
     * @param action the action to execute during the rollback
     * @return the current instance of {@link ScenarioRollbackTransitionConfigurer}
     */
    ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action);

    /**
     * Sets the Telegram request to be used during the rollback.
     *
     * @param telegramRequest the Telegram request to be sent during the rollback
     * @return the current instance of {@link ScenarioRollbackTransitionConfigurer}
     */
    ScenarioRollbackTransitionConfigurer<S> rollbackTelegramRequest(
        TelegramRequest telegramRequest);
}
