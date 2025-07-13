package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.action.StateDoActionPolicy;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.region.RegionExecutionPolicy;
import org.springframework.statemachine.transition.TransitionConflictPolicy;

/**
 * Base {@code ScenarioMonitoringConfigurer} interface for configuring state machine.
 *
 * @param <S> the type of state
 * @author Ivan Galushko
 */
public interface ScenarioConfigurationConfigurer<S> extends ScenarioConfigConfigurerBuilder<S> {

    /**
     * Specify a {@link BeanFactory}.
     *
     * @param beanFactory the bean factory
     * @return configurer for chaining
     */
    ScenarioConfigurationConfigurer<S> beanFactory(BeanFactory beanFactory);

    /**
     * Specify a {@link StateMachineListener} to be registered
     * with a state machine. This method can be called multiple times
     * to register multiple listeners.
     *
     * @param listener the listener to register
     * @return the configuration configurer
     */
    ScenarioConfigurationConfigurer<S> listener(StateMachineListener<S, ScenarioEvent> listener);

    /**
     * Specify a {@link TransitionConflictPolicy}. Default to {@link TransitionConflictPolicy#CHILD}.
     *
     * @param transitionConflictPolicy the transition conflict policy
     * @return the configuration configurer
     */
    ScenarioConfigurationConfigurer<S> transitionConflictPolicy(TransitionConflictPolicy transitionConflictPolicy);

    /**
     * Specify a {@link StateDoActionPolicy}. Defaults to {@link StateDoActionPolicy#IMMEDIATE_CANCEL}.
     *
     * @param stateDoActionPolicy the state do action policy
     * @return the configuration configurer
     */
    ScenarioConfigurationConfigurer<S> stateDoActionPolicy(StateDoActionPolicy stateDoActionPolicy);

    /**
     * Specify a timeout used with {@link StateDoActionPolicy}.
     *
     * @param timeout the timeout
     * @param unit the time unit
     * @return the configuration configurer
     */
    ScenarioConfigurationConfigurer<S> stateDoActionPolicyTimeout(long timeout, TimeUnit unit);

    /**
     * Specify a {@link RegionExecutionPolicy}. Default to {@link RegionExecutionPolicy#SEQUENTIAL}.
     *
     * @param regionExecutionPolicy the region execution policy
     * @return the configuration configurer
     */
    ScenarioConfigurationConfigurer<S> regionExecutionPolicy(RegionExecutionPolicy regionExecutionPolicy);
}
