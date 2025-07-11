package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.action.StateDoActionPolicy;
import org.springframework.statemachine.config.configurers.ConfigurationConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.region.RegionExecutionPolicy;
import org.springframework.statemachine.transition.TransitionConflictPolicy;

public class DefaultScenarioConfigurationConfigurer<S> implements ScenarioConfigurationConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final ConfigurationConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioConfigurationConfigurer(
        ScenarioBuilder<S> builder, ConfigurationConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> beanFactory(BeanFactory beanFactory) {
        configurer.beanFactory(beanFactory);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> listener(StateMachineListener<S, ScenarioEvent> listener) {
        configurer.listener(listener);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> transitionConflictPolicy(
        TransitionConflictPolicy transitionConflictPolicy) {
        configurer.transitionConflictPolicy(transitionConflictPolicy);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> stateDoActionPolicy(StateDoActionPolicy stateDoActionPolicy) {
        configurer.stateDoActionPolicy(stateDoActionPolicy);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> stateDoActionPolicyTimeout(long timeout, TimeUnit unit) {
        configurer.stateDoActionPolicyTimeout(timeout, unit);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> regionExecutionPolicy(RegionExecutionPolicy regionExecutionPolicy) {
        configurer.regionExecutionPolicy(regionExecutionPolicy);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> and() throws Exception {
        return new DefaultScenarioConfigConfigurer<>(builder, configurer.and());
    }
}
