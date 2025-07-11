package io.github.drednote.telegram.handler.scenario.spy;

import org.jetbrains.annotations.NotNull;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.config.StateMachineConfig;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelBuilder;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionBuilder;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.common.annotation.AnnotationBuilder;
import org.springframework.statemachine.config.common.annotation.ObjectPostProcessor;
import org.springframework.statemachine.config.model.ConfigurationData;
import org.springframework.statemachine.config.model.DefaultStateMachineModel;
import org.springframework.statemachine.config.model.StatesData;
import org.springframework.statemachine.config.model.TransitionsData;

public class ScenarioStateMachineBuilder {

    public static <S, E> Builder<S, E> builder() {
        return new InternalBuilder<>();
    }

    public static class InternalBuilder<S, E> extends Builder<S, E> {

        private final StateMachineConfigBuilder<S, E> builder;
        private final BuilderStateMachineConfigurerAdapter<S, E> adapter;

        /**
         * Instantiates a new builder.
         */
        public InternalBuilder() {
            adapter = new ScenarioStateMachineBuilder.BuilderStateMachineConfigurerAdapter<>();
            builder = new StateMachineConfigBuilder<>();
        }

        /**
         * Configure model.
         *
         * @return the state machine model configurer
         */
        public StateMachineModelConfigurer<S, E> configureModel() {
            return adapter.modelBuilder;
        }

        /**
         * Configure configuration.
         *
         * @return the state machine configuration configurer
         */
        public StateMachineConfigurationConfigurer<S, E> configureConfiguration() {
            return adapter.configurationBuilder;
        }

        /**
         * Configure states.
         *
         * @return the state machine state configurer
         */
        public StateMachineStateConfigurer<S, E> configureStates() {
            return adapter.stateBuilder;
        }

        /**
         * Configure transitions.
         *
         * @return the state machine transition configurer
         */
        public StateMachineTransitionConfigurer<S, E> configureTransitions() {
            return adapter.transitionBuilder;
        }

        @Override
        public StateMachineFactory<S, E> createFactory() {
            try {
                builder.apply(adapter);

                ScenarioStateMachineFactory<S, E> stateMachineFactory = getFactory();
                ConfigurationData<S, E> stateMachineConfigurationConfig = builder.getOrBuild().stateMachineConfigurationConfig;

                stateMachineFactory.setHandleAutostartup(stateMachineConfigurationConfig.isAutoStart());

                if (stateMachineConfigurationConfig.getBeanFactory() != null) {
                    stateMachineFactory.setBeanFactory(stateMachineConfigurationConfig.getBeanFactory());
                }
                return stateMachineFactory;
            } catch (Exception e) {
                throw new StateMachineException("Error creating state machine factory", e);
            }
        }

        @NotNull
        private ScenarioStateMachineFactory<S, E> getFactory() {
            StateMachineConfig<S, E> stateMachineConfig = builder.getOrBuild();

            TransitionsData<S, E> stateMachineTransitions = stateMachineConfig.getTransitions();
            StatesData<S, E> stateMachineStates = stateMachineConfig.getStates();
            ConfigurationData<S, E> stateMachineConfigurationConfig = stateMachineConfig.getStateMachineConfigurationConfig();

            ScenarioStateMachineFactory<S, E> stateMachineFactory;
            if (stateMachineConfig.getModel() != null && stateMachineConfig.getModel().getFactory() != null) {
                stateMachineFactory = new ScenarioStateMachineFactory<>(
                    new DefaultStateMachineModel<>(stateMachineConfigurationConfig, null, null),
                    stateMachineConfig.getModel().getFactory());
            } else {
                stateMachineFactory = new ScenarioStateMachineFactory<>(new DefaultStateMachineModel<>(
                    stateMachineConfigurationConfig, stateMachineStates, stateMachineTransitions), null);
            }
            return stateMachineFactory;
        }
    }

    private static class BuilderStateMachineConfigurerAdapter<S, E>
        implements StateMachineConfigurer<S, E> {

        private StateMachineModelBuilder<S, E> modelBuilder;
        private StateMachineTransitionBuilder<S, E> transitionBuilder;
        private StateMachineStateBuilder<S, E> stateBuilder;
        private StateMachineConfigurationBuilder<S, E> configurationBuilder;

        BuilderStateMachineConfigurerAdapter() {
            try {
                getStateMachineModelBuilder();
                getStateMachineTransitionBuilder();
                getStateMachineStateBuilder();
                getStateMachineConfigurationBuilder();
            } catch (Exception e) {
                throw new StateMachineException("Error instantiating builder adapter", e);
            }
        }

        @Override
        public void init(StateMachineConfigBuilder<S, E> config) throws Exception {
            config.setSharedObject(StateMachineModelBuilder.class, getStateMachineModelBuilder());
            config.setSharedObject(StateMachineTransitionBuilder.class, getStateMachineTransitionBuilder());
            config.setSharedObject(StateMachineStateBuilder.class, getStateMachineStateBuilder());
            config.setSharedObject(StateMachineConfigurationBuilder.class, getStateMachineConfigurationBuilder());
        }

        @Override
        public void configure(StateMachineConfigBuilder<S, E> builder) throws Exception {
        }

        @Override
        public boolean isAssignable(AnnotationBuilder<StateMachineConfig<S, E>> builder) {
            return false;
        }

        @Override
        public void configure(StateMachineModelConfigurer<S, E> model) throws Exception {
        }

        @Override
        public void configure(StateMachineConfigurationConfigurer<S, E> config) throws Exception {
        }

        @Override
        public void configure(StateMachineStateConfigurer<S, E> states) throws Exception {
        }

        @Override
        public void configure(StateMachineTransitionConfigurer<S, E> transitions) throws Exception {
        }

        protected final StateMachineModelBuilder<S, E> getStateMachineModelBuilder() throws Exception {
            if (modelBuilder != null) {
                return modelBuilder;
            }
            modelBuilder = new StateMachineModelBuilder<S, E>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
            configure(modelBuilder);
            return modelBuilder;
        }

        protected final StateMachineTransitionBuilder<S, E> getStateMachineTransitionBuilder() throws Exception {
            if (transitionBuilder != null) {
                return transitionBuilder;
            }
            transitionBuilder = new StateMachineTransitionBuilder<S, E>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
            return transitionBuilder;
        }

        protected final StateMachineStateBuilder<S, E> getStateMachineStateBuilder() throws Exception {
            if (stateBuilder != null) {
                return stateBuilder;
            }
            stateBuilder = new StateMachineStateBuilder<S, E>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
            return stateBuilder;
        }

        protected final StateMachineConfigurationBuilder<S, E> getStateMachineConfigurationBuilder() throws Exception {
            if (configurationBuilder != null) {
                return configurationBuilder;
            }
            configurationBuilder = new StateMachineConfigurationBuilder<S, E>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
            return configurationBuilder;
        }
    }
}
