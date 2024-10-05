package io.github.drednote.telegram.handler.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestAccessor;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.SimpleTransition;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.SimpleStateContext;
import io.github.drednote.telegram.utils.FieldProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScenarioTest {

    SimpleScenarioPersister<String> persister;
    SimpleScenarioFactory<String> factory;
    UpdateRequest updateRequest = Mockito.mock(UpdateRequest.class);
    ScenarioRepositoryAdapter<String> adapter = new MockAdapter<>();
    private SimpleState<String> nextState2;

    @BeforeEach
    void setUp() {
        persister = new SimpleScenarioPersister<>(FieldProvider.create(adapter));
        when(updateRequest.getAccessor()).thenReturn(Mockito.mock(UpdateRequestAccessor.class));

        Map<State<String>, List<Transition<String>>> states = new HashMap<>();

        SimpleState<String> initialState = new SimpleState<>("1");
        nextState2 = new SimpleState<>("2");
        SimpleState<String> nextState3 = new SimpleState<>("3",
            Set.of(new UpdateRequestMapping("**", RequestType.MESSAGE, Set.of())));
        SimpleState<String> nextState4 = new SimpleState<>("4",
            Set.of(new UpdateRequestMapping("**", RequestType.MESSAGE, Set.of())));

        SimpleTransition<String> transitionFrom1To2 = new SimpleTransition<>(initialState,
            new SimpleState<>("2", Set.of(new UpdateRequestMapping("**", RequestType.POLL, Set.of()))));
        states.put(initialState, List.of(
            transitionFrom1To2, new SimpleTransition<>(initialState, nextState3)));
        states.put(nextState2, List.of(new SimpleTransition<>(nextState2, nextState4)));

        factory = new SimpleScenarioFactory<>(
            new SimpleScenarioConfig<>(initialState, states, new SimpleScenarioIdResolver(FieldProvider.empty())),
            persister);

        when(updateRequest.getRequestType()).thenReturn(RequestType.MESSAGE);
    }

    @Test
    void shouldCorrectChooseNextStateIfPersisterEmpty() {
        String id = "1";
        Scenario<String> scenario = factory.create(id);
        persister.restore(scenario, id);

        boolean sendEvent = scenario.sendEvent(updateRequest);

        assertThat(sendEvent).isTrue();
        assertThat(scenario.getState().getId()).isEqualTo("3");
    }

    @Test
    void shouldCorrectChooseNextStateIfPersisterNotEmpty() throws IOException {
        String id = "2";
        adapter.save(new SimpleScenarioContext<>(id,
            new SimpleStateContext<>(nextState2.getId(),
                Set.of(new UpdateRequestMapping("**", RequestType.POLL, Set.of())), false)));

        Scenario<String> scenario = factory.create(id);
        persister.restore(scenario, id);

        boolean sendEvent = scenario.sendEvent(updateRequest);

        assertThat(sendEvent).isTrue();
        assertThat(scenario.getState().getId()).isEqualTo("4");
    }

    static class MockAdapter<S> implements ScenarioRepositoryAdapter<S> {

        private final Map<String, ScenarioContext<S>> context = new HashMap<>();

        @Override
        public Optional<? extends ScenarioContext<S>> findById(String id) {
            return Optional.ofNullable(context.get(id));
        }

        @Override
        public void changeId(ScenarioContext<S> context, String oldId) throws IOException {

        }

        @Override
        public void save(ScenarioContext<S> persistContext) {
            context.put(persistContext.id(), persistContext);
        }
    }
}