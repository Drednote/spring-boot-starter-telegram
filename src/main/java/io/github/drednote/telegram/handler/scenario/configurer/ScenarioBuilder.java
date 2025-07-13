package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.spy.CompositeScenarioStateMachineMonitor;
import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineBuilder.ScenarioMachineBuilder;
import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineMonitor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public class ScenarioBuilder<S> {

    private final ScenarioMachineBuilder<S> machineBuilder;
    @Nullable
    @Setter
    private ScenarioIdResolver resolver;
    @Nullable
    @Setter
    private ScenarioPersister<S> persister;
    @Getter
    @Setter
    private S initialState;
    @Getter
    private final Set<S> states = new HashSet<>();
    private final List<ScenarioStateMachineMonitor<S>> monitors = new ArrayList<>();

    public ScenarioBuilder(ScenarioMachineBuilder<S> machineBuilder) {
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

    public ScenarioStateMachineMonitor<S> getMonitor() {
        return new CompositeScenarioStateMachineMonitor<>(monitors);
    }

    public void addState(S state) {
        if (state != null) {
            states.add(state);
        }
    }

    public void addMonitor(ScenarioStateMachineMonitor<S> monitor) {
        if (monitor != null) {
            this.monitors.add(monitor);
        }
    }

    public ScenarioData<S> build() throws Exception {
        configureConfiguration().withConfiguration().autoStartup(true);

        StateMachineFactory<S, ScenarioEvent> factory = machineBuilder.createFactory(this);

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
