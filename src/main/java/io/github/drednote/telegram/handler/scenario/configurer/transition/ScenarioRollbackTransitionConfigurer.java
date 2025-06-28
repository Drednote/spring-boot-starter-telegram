package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import io.github.drednote.telegram.handler.scenario.machine.Guard;
import java.util.Map;

/**
 * Interface for configuring rollback transitions in scenarios.
 * <p>
 * During this configurer will be created a new one transition with a reverse direction. For example, you create A → B
 * transition, there will be created B → A transition with personal telegramRequest matching and action (you should
 * specify it).
 *
 * @param <S> the type of the scenario
 * @author Ivan Galushko
 */
public interface ScenarioRollbackTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioRollbackTransitionConfigurer<S>, S> {

    /**
     * Sets the target state for the transition.
     *
     * @param target the target getMachine for the transition
     * @return the current instance of the configurer
     */
    ScenarioRollbackTransitionConfigurer<S> target(S target);

    /**
     * Sets the action to be performed during the rollback.
     *
     * @param action the action to execute during the rollback
     * @return the current instance of {@link ScenarioRollbackTransitionConfigurer}
     */
    ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action);

    ScenarioRollbackTransitionConfigurer<S> rollbackAction(Action<S> action, Action<S> error);

    /**
     * Sets the Telegram request to be used during the rollback.
     *
     * @param telegramRequest the Telegram request to be sent during the rollback
     * @return the current instance of {@link ScenarioRollbackTransitionConfigurer}
     */
    ScenarioRollbackTransitionConfigurer<S> rollbackTelegramRequest(TelegramRequest telegramRequest);

    ScenarioRollbackTransitionConfigurer<S> rollbackGuard(Guard<S> guard);

    /**
     * Sets the additional props to be used during the rollback.
     *
     * @param props additional props to pass to {@link Action} in {@link ActionContext}
     * @return the current instance of {@link ScenarioRollbackTransitionConfigurer}
     */
    ScenarioRollbackTransitionConfigurer<S> rollbackProps(Map<String, Object> props);
}
