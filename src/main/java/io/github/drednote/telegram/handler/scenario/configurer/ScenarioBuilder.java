package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public class ScenarioBuilder<S> {

    private final Builder<S, ScenarioEvent> machineBuilder;
    @Nullable
    @Setter
    private ScenarioIdResolver resolver;
    @Nullable
    @Setter
    private ScenarioPersister<S> persister;
    @Getter
    @Setter
    private S initialState;

    public ScenarioBuilder(Builder<S, ScenarioEvent> machineBuilder) {
        this.machineBuilder = machineBuilder;
    }

    public StateMachineConfigurationConfigurer<S, ScenarioEvent> configureConfiguration() {
        return machineBuilder.configureConfiguration();
    }

    public StateMachineStateConfigurer<S, ScenarioEvent> configureStates() {
        return machineBuilder.configureStates();
    }

    public StateMachineTransitionConfigurer<S, ScenarioEvent> configureTransitions() {
        return machineBuilder.configureTransitions();
    }

    public ScenarioData<S> build() throws Exception {
        configureConfiguration().withConfiguration().autoStartup(true);

        StateMachineFactory<S, ScenarioEvent> factory = machineBuilder.createFactory();

        return new ScenarioData<>(resolver, persister, factory);
    }

    public record ScenarioData<S>(
        @Nullable
        ScenarioIdResolver resolver,
        @Nullable
        ScenarioPersister<S> persister,
        @NonNull
        StateMachineFactory<S, ScenarioEvent> factory
    ) {}
}
