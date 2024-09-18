package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.SimpleTransition;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.utils.Assert;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;
import org.springframework.lang.Nullable;

public class ScenarioBuilder<S> {

    @Nullable
    @Setter
    private S initial;
    @Nullable
    @Setter
    private ScenarioRepositoryAdapter<S> adapter;
    @Nullable
    @Setter
    private ScenarioIdResolver resolver;
    private final Set<S> terminalStates = new HashSet<>();
    private final List<TransitionData<S>> transitions = new ArrayList<>();

    public void addTransition(TransitionData<S> transition) {
        transitions.add(transition);
    }

    public void addTerminalState(S state) {
        terminalStates.add(state);
    }

    public ScenarioData<S> build() {
        Assert.required(initial, "Initial state");

        Map<State<S>, List<Transition<S>>> states = buildStates(transitions);

        SimpleState<S> initialState = new SimpleState<>(initial);
        Set<State<S>> terminalStates = this.terminalStates.stream().map(SimpleState::new).collect(Collectors.toSet());

        return new ScenarioData<>(
            initialState, states, terminalStates,
            FieldProvider.create(this.adapter),
            resolver
        );
    }

    static <S> Map<State<S>, List<Transition<S>>> buildStates(
        List<TransitionData<S>> transitionData
    ) {
        Set<Transition<S>> allTransitions = new HashSet<>();
        Map<State<S>, List<Transition<S>>> states = new HashMap<>();
        transitionData.forEach(t -> {
            SimpleState<S> source = new SimpleState<>(t.getSource());
            SimpleState<S> target = new SimpleState<>(t.getTarget());
            initTarget(target, t);
            SimpleTransition<S> transition = new SimpleTransition<>(source, target);
            if (!allTransitions.add(transition)) {
                throw new IllegalStateException(
                    "\nAmbiguous transition. Cannot create a new transition because one already exists %s."
                        .formatted(transition));
            }
            states.computeIfAbsent(source, key -> new ArrayList<>()).add(transition);
        });
        return states;
    }

    private static <S> void initTarget(
        SimpleState<S> target, TransitionData<S> transition
    ) {
        TelegramRequest request = transition.getRequest();
        Set<UpdateRequestMapping> mappings = new HashSet<>();
        UpdateRequestMappingBuilder builder = new UpdateRequestMappingBuilder(request);
        builder.forEach(mappings::add);
        if (mappings.isEmpty()) {
            throw new IllegalStateException("There are no condition to match for state " + target);
        }
        target.setActions(transition.getActions());
        target.setMappings(mappings);
        target.setCallbackQueryState(transition.isCallBackQuery());
        target.setOverrideGlobalScenarioId(transition.isOverrideGlobalScenarioId());
    }

    public record ScenarioData<S>(
        State<S> initialState,
        Map<State<S>, List<Transition<S>>> states,
        Set<State<S>> terminalStates,
        FieldProvider<ScenarioRepositoryAdapter<S>> adapter,
        @Nullable
        ScenarioIdResolver resolver
    ) {}
}
