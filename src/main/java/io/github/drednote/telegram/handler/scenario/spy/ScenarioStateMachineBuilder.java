package io.github.drednote.telegram.handler.scenario.spy;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.config.StateMachineConfig;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurer;
import org.springframework.statemachine.config.model.ConfigurationData;
import org.springframework.statemachine.config.model.DefaultStateMachineModel;
import org.springframework.statemachine.config.model.StatesData;
import org.springframework.statemachine.config.model.TransitionsData;

public class ScenarioStateMachineBuilder {

    public static <S> ScenarioMachineBuilder<S> builder() {
        return new ScenarioMachineBuilder<>();
    }

    public static class ScenarioMachineBuilder<S> extends Builder<S, ScenarioEvent> {

        @Override
        public StateMachineFactory<S, ScenarioEvent> createFactory() {
            throw new UnsupportedOperationException("Call createFactory(ScenarioBuilder<S>) instead of this method");
        }

        public StateMachineFactory<S, ScenarioEvent> createFactory(ScenarioBuilder<S> scenarioBuilder) {
            try {
                StateMachineConfigBuilder<S, ScenarioEvent> builder = getField("builder");
                StateMachineConfigurer<S, ScenarioEvent> adapter = getField("adapter");
                builder.apply(adapter);

                ScenarioStateMachineFactory<S> stateMachineFactory = getFactory(builder, scenarioBuilder);
                ConfigurationData<S, ScenarioEvent> stateMachineConfigurationConfig = builder.getOrBuild().stateMachineConfigurationConfig;

                stateMachineFactory.setHandleAutostartup(stateMachineConfigurationConfig.isAutoStart());

                if (stateMachineConfigurationConfig.getBeanFactory() != null) {
                    stateMachineFactory.setBeanFactory(stateMachineConfigurationConfig.getBeanFactory());
                }
                return stateMachineFactory;
            } catch (Exception ScenarioEvent) {
                throw new StateMachineException("Error creating state machine factory", ScenarioEvent);
            }
        }

        @NotNull
        private ScenarioStateMachineFactory<S> getFactory(
            StateMachineConfigBuilder<S, ScenarioEvent> builder, ScenarioBuilder<S> scenarioBuilder
        ) {
            StateMachineConfig<S, ScenarioEvent> stateMachineConfig = builder.getOrBuild();

            TransitionsData<S, ScenarioEvent> stateMachineTransitions = stateMachineConfig.getTransitions();
            StatesData<S, ScenarioEvent> stateMachineStates = stateMachineConfig.getStates();
            ConfigurationData<S, ScenarioEvent> stateMachineConfigurationConfig = stateMachineConfig.getStateMachineConfigurationConfig();

            ScenarioStateMachineFactory<S> stateMachineFactory;
            if (stateMachineConfig.getModel() != null && stateMachineConfig.getModel().getFactory() != null) {
                stateMachineFactory = new ScenarioStateMachineFactory<>(
                    new DefaultStateMachineModel<>(stateMachineConfigurationConfig, null, null),
                    stateMachineConfig.getModel().getFactory(), scenarioBuilder);
            } else {
                stateMachineFactory = new ScenarioStateMachineFactory<>(new DefaultStateMachineModel<>(
                    stateMachineConfigurationConfig, stateMachineStates, stateMachineTransitions), null,
                    scenarioBuilder);
            }
            return stateMachineFactory;
        }

        private <T> T getField(String name) throws NoSuchFieldException, IllegalAccessException {
            Field declaredBuilder = Builder.class.getDeclaredField(name);
            declaredBuilder.setAccessible(true);
            return (T) declaredBuilder.get(this);
        }
    }
}
