package io.github.drednote.telegram.handler.scenario;

import static io.github.drednote.telegram.handler.scenario.DefaultScenario.INLINE_KEYBOARD_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.TelegramRequests;
import io.github.drednote.telegram.datasource.scenarioid.InMemoryScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.config.DefaultScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.DefaultScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
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
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;

@Slf4j
public class DefaultScenarioTest {

    AtomicReference<Scenario<?>> atomicScenario = new AtomicReference<>();

    @Test
    void shouldCorrectConfigureScenario() throws Throwable {
        TestScenarioAdapter adapter = new TestScenarioAdapter();
        ScenarioBuilder<State> builder = new ScenarioBuilder<>(StateMachineBuilder.builder());
        adapter.onConfigure(new DefaultScenarioStateConfigurer<>(builder, new HashSet<>()));
        adapter.onConfigure(new DefaultScenarioTransitionConfigurer<>(builder));
        adapter.onConfigure(new DefaultScenarioConfigConfigurer<>(builder));
        ScenarioData<State> data = builder.build();

        ScenarioUpdateHandlerPopular<State> handlerPopular = getPopular(data.factory());

        DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
            UpdateBuilder._default("hello Ivan").message());
        handlerPopular.preFilter(request);
        Scenario<?> scenario = request.getScenario();
        atomicScenario.compareAndSet(null, scenario);
        assertThat(scenario).isNotNull();

        ScenarioEventResult<?, ScenarioEvent> result = scenario.sendEvent(request);
        assertThat(result).isNotNull();
        if (result.exception() != null) {
            throw result.exception();
        }
        assertThat(result.success()).isTrue();

        Boolean property = scenario.getProperty(INLINE_KEYBOARD_PROPERTY);
        assertThat(property).isTrue();
    }

    private static @NotNull ScenarioUpdateHandlerPopular<State> getPopular(
        StateMachineFactory<State, ScenarioEvent> machineFactory) {
        ScenarioIdResolver resolver = new DefaultScenarioIdResolver(new InMemoryScenarioIdRepositoryAdapter());

        ScenarioPersister<State> persister = new InMemoryScenarioPersister<>();
        ScenarioFactory<State> factory = new MachineScenarioFactory<>(machineFactory, persister, resolver);
        return new ScenarioUpdateHandlerPopular<>(persister, factory, resolver);
    }

    class TestScenarioAdapter extends ScenarioConfigurerAdapter<State> {

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
        public void onConfigure(ScenarioConfigConfigurer<State> configurer) throws Exception {
//            configurer.withMonitoring().monitor((scenario, transition, duration) -> {
//                assertThat(atomicScenario.get()).isEqualTo(scenario);
//            });
        }

        @Override
        public void onConfigure(ScenarioStateConfigurer<State> configurer) throws Exception {
            configurer.withStates().initial(State.ONE).states(EnumSet.allOf(State.class));
        }
    }

    enum State {
        ONE, TWO, THREE
    }
}
