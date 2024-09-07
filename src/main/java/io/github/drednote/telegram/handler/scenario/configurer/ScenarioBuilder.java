package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ScenarioConfig;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.SimpleScenarioConfig;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.SimpleTransition;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioTransitionConfigurer.TransitionData;
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
        Assert.required(resolver, "Scenario id resolver");

        Map<S, SimpleState<S>> uniqueStates = new HashMap<>();
        Map<State<S>, List<Transition<S>>> states = buildStates(uniqueStates, transitions);

        SimpleState<S> initialState = uniqueStates.get(initial);
        Set<State<S>> terminalStates = this.terminalStates.stream().map(uniqueStates::get).collect(Collectors.toSet());

        if (initialState == null) {
            throw new IllegalStateException("Initial state is null");
        }

        return new ScenarioData<>(
                new SimpleScenarioConfig<>(initialState, states, terminalStates),
                FieldProvider.create(this.adapter),
                resolver
        );
    }

    static <S> Map<State<S>, List<Transition<S>>> buildStates(
            Map<S, SimpleState<S>> uniqueStates, List<TransitionData<S>> transitions
    ) {
        Map<State<S>, List<Transition<S>>> states = new HashMap<>();
        Map<State<S>, Set<UpdateRequestMapping>> mappings = new HashMap<>();
        transitions.forEach(t -> {
            SimpleState<S> source = uniqueStates.computeIfAbsent(t.source(), SimpleState::new);
            SimpleState<S> target = uniqueStates.computeIfAbsent(t.target(), SimpleState::new);
            initTarget(target, source, t.actions(), t.request(), mappings);
            states.computeIfAbsent(source, key -> new ArrayList<>()).add(new SimpleTransition<>(source, target));
        });
        return states;
    }

    private static <S> void initTarget(
            SimpleState<S> target, SimpleState<S> source, List<Action> actions, TelegramRequest request,
            Map<State<S>, Set<UpdateRequestMapping>> mappings
    ) {
        Set<UpdateRequestMapping> curMappings = mappings.computeIfAbsent(source, key -> new HashSet<>());
        UpdateRequestMappingBuilder builder = new UpdateRequestMappingBuilder(request);
        builder.forEach(mapping -> {
            if (!curMappings.add(mapping)) {
                throw buildException(target, mapping);
            }
        });
        if (curMappings.isEmpty()) {
            throw new IllegalStateException("There are no condition to match for state " + target);
        }
        target.setActions(actions);
        target.setMappings(curMappings);
    }

    private static <S> IllegalStateException buildException(State<S> state, UpdateRequestMapping mapping) {
        return new IllegalStateException(
                "\nAmbiguous mapping. Cannot map to state '" + state + "' mapping \n" +
                mapping + ": It is already mapped.");
    }

    public record ScenarioData<S>(
            ScenarioConfig<S> scenarioConfig,
            FieldProvider<ScenarioRepositoryAdapter<S>> adapter,
            ScenarioIdResolver resolver
    ) {}
}
