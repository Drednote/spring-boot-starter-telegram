package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class SimpleScenarioPersister<S> implements ScenarioPersister<S> {

    private final FieldProvider<? extends ScenarioRepositoryAdapter<S>> adapterProvider;

    @Override
    public void persist(Scenario<S> context) {
        try {
            adapterProvider.ifExistsWithException(adapter -> adapter.save(convert(context)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restore(Scenario<S> scenario, String scenarioId) {
        adapterProvider.toOptional()
            .flatMap(adapter -> adapter.findById(scenarioId))
            .ifPresent(context -> scenario.getAccessor().resetScenario(context));
    }

    private ScenarioContext<S> convert(Scenario<S> scenario) {
        State<S> state = scenario.getState();
        StateContext<S> stateContext = convertToStateContext(state);
        return new SimpleScenarioContext<>(scenario.getId(), stateContext);
    }

    private @NonNull StateContext<S> convertToStateContext(State<S> state) {
        Set<? extends UpdateRequestMappingAccessor> requestMappings = state.getMappings();
        return new SimpleStateContext<>(
            state.getId(), requestMappings, state.isResponseMessageProcessing()
        );
    }
}
