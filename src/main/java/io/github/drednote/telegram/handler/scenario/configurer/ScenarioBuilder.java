package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
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
    private ScenarioRepositoryAdapter<S> adapter;
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

    @SneakyThrows
    public void addTransition(TransitionData<S> transition) {
//        var configurer = new SimpleScenarioTransitionConfigurer<>(this).withExternal();
//        buildTransition(
//            configurer, transition.getSource(), null, transition.getRequest(),
//            null, null, null, null, null,
//            null, null, convert(transition.getActions()), transition.getProps()
//        );
//        if (transition.getTarget() != null) {
//            configurer.state(transition.getTarget());
//        }
//        configurer.and();
    }

    @Nullable
    private List<Pair<Action<S>, Action<S>>> convert(@Nullable List<Action<S>> actions) {
        if (actions == null) {
            return null;
        }
        List<Pair<Action<S>, Action<S>>> list = new ArrayList<>();
        for (Action<S> a : actions) {
            Pair<Action<S>, Action<S>> actionObjectPair = Pair.of(a, null);
            list.add(actionObjectPair);
        }
        return list;
    }

    public ScenarioData<S> build() throws Exception {
        if (adapter != null && persister != null) {
            throw new IllegalStateException("adapter and persister cannot be used together.");
        }

        configureConfiguration().withConfiguration().autoStartup(true);

        return new ScenarioData<>(
            this.adapter, resolver, persister, machineBuilder.createFactory()
        );
    }


    public record ScenarioData<S>(
//        ScenarioState<S> initialState,
//        Map<S, List<Transition<S>>> states,
//        Set<S> terminalStates,
        @Nullable
        ScenarioRepositoryAdapter<S> adapter,
        @Nullable
        ScenarioIdResolver resolver,
        @Nullable
        ScenarioPersister<S> persister,
        @NonNull
        StateMachineFactory<S, ScenarioEvent> factory
    ) {}
}
