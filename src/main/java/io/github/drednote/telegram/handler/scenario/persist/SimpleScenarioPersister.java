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
    public void changeId(Scenario<S> context, String newId) {
        try {
            String oldId = context.getId();
            context.getAccessor().setId(newId);
            adapterProvider.ifExistsWithException(adapter -> adapter.changeId(convert(context), oldId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scenario<S> restore(Scenario<S> scenario, String scenarioId) {
        return adapterProvider.toOptional()
            .flatMap(adapter -> adapter.findById(scenarioId))
            .map(context -> doRestore(context, scenario))
            .orElse(scenario);
    }

    private Scenario<S> doRestore(ScenarioContext<S> context, Scenario<S> scenario) {
        scenario.getAccessor().resetScenario(context);
        return scenario;
    }

    private ScenarioContext<S> convert(Scenario<S> scenario) {
        State<S> state = scenario.getState();
        StateContext<S> stateContext = convertToStateContext(state);
        return new SimpleScenarioContext<>(scenario.getId(), stateContext);
    }

    private @NonNull StateContext<S> convertToStateContext(State<S> state) {
        Set<? extends UpdateRequestMappingAccessor> requestMappings = state.getUpdateRequestMappings();
        return new SimpleStateContext<>(
            state.getId(), requestMappings, state.isCallbackQueryState(), state.isOverrideGlobalScenarioId()
        );
    }
}
