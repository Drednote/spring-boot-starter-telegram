package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.ScenarioChoiceTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.ScenarioJunctionTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioEntryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioExitTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioForkTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioHistoryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioJoinTransitionConfigurer;

/**
 * Interface for configuring scenario transitions.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioTransitionConfigurer<S> {

    /**
     * Configures an external transition.
     * <p>
     * This is the default behavior for transition, which is just creating transition with specified parameters.
     *
     * @return a {@link ScenarioExternalTransitionConfigurer} instance for further configuration
     */
    ScenarioExternalTransitionConfigurer<S> withExternal() throws Exception;

    /**
     * Configures a rollback transition.
     * <p>
     * During this configurer will be created a new one transition with a reverse direction. For example, you create A →
     * B transition, there will be created B → A transition with personal telegramRequest matching and action (you
     * should specify it).
     *
     * @return a {@link ScenarioRollbackTransitionConfigurer} instance for further configuration
     */
    ScenarioRollbackTransitionConfigurer<S> withRollback() throws Exception;

    /**
     * Gets a configurer for internal transition. Internal transition is used when action needs to be executed without
     * causing a state transition. With internal transition source and target state is always a same and it is identical
     * with self-transition in the absence of state entry and exit actions.
     *
     * @return {@link ScenarioInternalTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioInternalTransitionConfigurer<S> withInternal() throws Exception;

    /**
     * Gets a configurer for local transition. Local transition doesn’t cause exit and entry to source state if target
     * state is a substate of a source state. Other way around, local transition doesn’t cause exit and entry to target
     * state if target is a superstate of a source state.
     *
     * @return {@link ScenarioLocalTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioLocalTransitionConfigurer<S> withLocal() throws Exception;

    /**
     * Gets a configurer for transition from a choice pseudostate.
     *
     * @return {@link ScenarioChoiceTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioChoiceTransitionConfigurer<S> withChoice() throws Exception;

    /**
     * Gets a configurer for transition from a junction pseudostate.
     *
     * @return {@link ScenarioJunctionTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioJunctionTransitionConfigurer<S> withJunction() throws Exception;

    /**
     * Gets a configurer for transition from a fork pseudostate.
     *
     * @return {@link ScenarioForkTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioForkTransitionConfigurer<S> withFork() throws Exception;

    /**
     * Gets a configurer for transition from a join pseudostate.
     *
     * @return {@link ScenarioJoinTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioJoinTransitionConfigurer<S> withJoin() throws Exception;

    /**
     * Gets a configurer for transition from an entrypoint pseudostate.
     *
     * @return {@link ScenarioEntryTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioEntryTransitionConfigurer<S> withEntry() throws Exception;

    /**
     * Gets a configurer for transition from an exitpoint pseudostate.
     *
     * @return {@link ScenarioExitTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioExitTransitionConfigurer<S> withExit() throws Exception;

    /**
     * Gets a configurer for default history transition.
     *
     * @return {@link ScenarioHistoryTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioHistoryTransitionConfigurer<S> withHistory() throws Exception;
}
