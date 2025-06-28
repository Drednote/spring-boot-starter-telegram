//package io.github.drednote.telegram.handler.scenario;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//import io.github.drednote.telegram.core.request.RequestType;
//import io.github.drednote.telegram.core.request.UpdateRequest;
//import io.github.drednote.telegram.core.request.UpdateRequestAccessor;
//import io.github.drednote.telegram.core.request.UpdateRequestMapping;
//import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
//import io.github.drednote.telegram.handler.scenario.data.SimpleScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.SimpleTransition;
//import io.github.drednote.telegram.handler.scenario.data.ScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.Transition;
//import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
//import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.DefaultScenarioContext;
//import io.github.drednote.telegram.handler.scenario.factory.MachineScenarioFactory;
//import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioPersister;
//import io.github.drednote.telegram.handler.scenario.persist.SimpleStateContext;
//import io.github.drednote.telegram.utils.FieldProvider;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//class ScenarioTest {
//
//    SimpleScenarioPersister<String> persister;
//    MachineScenarioFactory<String> factory;
//    UpdateRequest updateRequest = Mockito.mock(UpdateRequest.class);
//    ScenarioRepositoryAdapter<String> adapter = new MockAdapter<>();
//    private SimpleScenarioState<String> nextState2;
//
//    @BeforeEach
//    void setUp() {
//        persister = new SimpleScenarioPersister<>(FieldProvider.create(adapter));
//        when(updateRequest.getAccessor()).thenReturn(Mockito.mock(UpdateRequestAccessor.class));
//
//        Map<ScenarioState<String>, List<Transition<String>>> states = new HashMap<>();
//
//        SimpleScenarioState<String> initialState = new SimpleScenarioState<>("1");
//        nextState2 = new SimpleScenarioState<>("2");
//        SimpleScenarioState<String> nextState3 = new SimpleScenarioState<>("3",
//            Set.of(new UpdateRequestMapping("**", RequestType.MESSAGE, Set.of())), new HashMap<>());
//        SimpleScenarioState<String> nextState4 = new SimpleScenarioState<>("4",
//            Set.of(new UpdateRequestMapping("**", RequestType.MESSAGE, Set.of())), new HashMap<>());
//
//        SimpleTransition<String> transitionFrom1To2 = new SimpleTransition<>(initialState,
//            new SimpleScenarioState<>("2", Set.of(new UpdateRequestMapping("**", RequestType.POLL, Set.of())), new HashMap<>()));
//        states.put(initialState, List.of(
//            transitionFrom1To2, new SimpleTransition<>(initialState, nextState3)));
//        states.put(nextState2, List.of(new SimpleTransition<>(nextState2, nextState4)));
//
//        factory = new MachineScenarioFactory<>(
//            new SimpleScenarioConfig<>(initialState, states, new DefaultScenarioIdResolver(FieldProvider.empty())),
//            persister);
//
//        when(updateRequest.getRequestType()).thenReturn(RequestType.MESSAGE);
//    }
//
//    @Test
//    void shouldCorrectChooseNextStateIfPersisterEmpty() {
//        String id = "1";
//        Scenario<String> scenario = factory.create(id);
//        persister.restore(scenario, id);
//
//        ScenarioEventResult sendEvent = scenario.sendEvent(updateRequest);
//
//        assertThat(sendEvent.success()).isTrue();
//        assertThat(scenario.getState().getId()).isEqualTo("3");
//    }
//
//    @Test
//    void shouldCorrectChooseNextStateIfPersisterNotEmpty() throws IOException {
//        String id = "2";
//        adapter.save(new DefaultScenarioContext<>(id,
//            new SimpleStateContext<>(nextState2.getId(),
//                Set.of(new UpdateRequestMapping("**", RequestType.POLL, Set.of())), false, new HashMap<>())));
//
//        Scenario<String> scenario = factory.create(id);
//        persister.restore(scenario, id);
//
//        ScenarioEventResult sendEvent = scenario.sendEvent(updateRequest);
//
//        assertThat(sendEvent.success()).isTrue();
//        assertThat(scenario.getState().getId()).isEqualTo("4");
//    }
//
//    static class MockAdapter<S> implements ScenarioRepositoryAdapter<S> {
//
//        private final Map<String, ScenarioContext<S>> context = new HashMap<>();
//
//        @Override
//        public Optional<? extends ScenarioContext<S>> findById(String id) {
//            return Optional.ofNullable(context.get(id));
//        }
//
//        @Override
//        public void save(ScenarioContext<S> persistContext) {
//            context.put(persistContext.getId(), persistContext);
//        }
//    }
//}