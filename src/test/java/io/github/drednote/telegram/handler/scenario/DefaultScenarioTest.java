package io.github.drednote.telegram.handler.scenario;

import static io.github.drednote.telegram.handler.scenario.DefaultScenario.RESPONSE_PROCESSING_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.TelegramRequests;
import io.github.drednote.telegram.datasource.scenarioid.InMemoryScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.scenario.configurer.DefaultScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEventResult;
import io.github.drednote.telegram.handler.scenario.factory.DefaultScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.factory.MachineScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.InMemoryScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.util.EnumSet;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;

@Slf4j
public class DefaultScenarioTest {

    @Test
    void shouldCorrectConfigureScenario() throws Throwable {
        TestScenarioAdapter adapter = new TestScenarioAdapter();
        ScenarioBuilder<State> builder = new ScenarioBuilder<>(StateMachineBuilder.builder());
        var stateConfigurer = new DefaultScenarioStateConfigurer<>(builder);
        stateConfigurer.withStates().initial(State.ONE).states(EnumSet.allOf(State.class));
        builder.configureConfiguration().withConfiguration().autoStartup(true);
        adapter.onConfigure(new DefaultScenarioTransitionConfigurer<>(builder));
        ScenarioData<State> data = builder.build();

        ScenarioUpdateHandlerPopular<State> handlerPopular = getPopular(data.factory());

        DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
            UpdateBuilder._default("hello Ivan").message());
        handlerPopular.preFilter(request);
        Scenario<?> scenario = request.getScenario();
        assertThat(scenario).isNotNull();

        ScenarioEventResult<?, ScenarioEvent> result = scenario.sendEvent(request);
        assertThat(result).isNotNull();
        if (result.exception() != null) {
            throw result.exception();
        }
        assertThat(result.success()).isTrue();

        Boolean property = scenario.getProperty(RESPONSE_PROCESSING_PROPERTY);
        assertThat(property).isTrue();
    }

    private static @NotNull ScenarioUpdateHandlerPopular<State> getPopular(
        StateMachineFactory<State, ScenarioEvent> machineFactory) {
        ScenarioIdResolver resolver = new DefaultScenarioIdResolver(new InMemoryScenarioIdRepositoryAdapter());

        ScenarioPersister<State> persister = new InMemoryScenarioPersister<>();
        ScenarioFactory<State> factory = new MachineScenarioFactory<>(machineFactory, persister, resolver);
        return new ScenarioUpdateHandlerPopular<>(persister, factory, resolver);
    }

    static class TestScenarioAdapter extends ScenarioConfigurerAdapter<State> {

        @Override
        public void onConfigure(ScenarioTransitionConfigurer<State> configurer) throws Exception {
            configurer.withExternal().inlineKeyboardCreation()
                .source(State.ONE).target(State.TWO)
                .action(a -> {
                    Map<String, String> templateVariables = a.getTemplateVariables();
                    assertThat(templateVariables.get("name")).isEqualTo("Ivan");
                    assertThat(a.getProps().get("age")).isEqualTo(21);
                    return null;
                })
                .telegramRequest(TelegramRequests.text("hello {name:.*}"))
                .props(Map.of("age", 21))
                .and();
        }

        @Override
        public void onConfigure(ScenarioConfigConfigurer<State> configurer) {

        }

        @Override
        public void onConfigure(ScenarioStateConfigurer<State> configurer) {

        }
    }

    enum State {
        ONE, TWO, THREE
    }
}
