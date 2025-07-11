package io.github.drednote.telegram.handler.scenario.property;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.DefaultScenario;
import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.configurer.state.DefaultScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.state.StateConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurerTest.TestScenarioFactory;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
    ConfigurationPropertiesAutoConfiguration.class, TestScenarioFactory.class,
    ScenarioFactoryContainer.class, ScenarioFactoryBeanPostProcessor.class
})
@EnableConfigurationProperties(ScenarioProperties.class)
@ActiveProfiles("scenarioproperties")
class ScenarioPropertiesConfigurerTest {

    @Autowired
    private ScenarioProperties scenarioProperties;
    @Autowired
    private ScenarioFactoryContainer scenarioFactoryContainer;
    private static boolean executed = false;

    @Test
    void shouldCorrectCreateTransitionsFromProperties() throws Throwable {
        assertThat(scenarioFactoryContainer.resolveAction(
            "io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurerTest$TestScenarioFactory#name(ActionContext)")).isNotNull();
        assertThat(scenarioFactoryContainer.resolveAction("test_name")).isNotNull();

        ScenarioBuilder<Object> scenarioBuilder = new ScenarioBuilder<>(StateMachineBuilder.builder());

        ScenarioPropertiesConfigurer<Object> configurer = new ScenarioPropertiesConfigurer<>(scenarioBuilder,
            scenarioProperties,
            scenarioFactoryContainer);
        Set<Object> states = configurer.collectStates();

        DefaultScenarioStateConfigurer<Object> scenarioStateConfigurer = new DefaultScenarioStateConfigurer<>(
            scenarioBuilder, states);
        StateConfigurer<Object> stateConfigurer = scenarioStateConfigurer.withStates();
        stateConfigurer.initial(StateEnum.INITIAL);
        configurer.configure();

        ScenarioData<Object> build = scenarioBuilder.build();
        assertThat(build).isNotNull();
        StateMachine<Object, ScenarioEvent> machine = build.factory().getStateMachine();
        DefaultScenario<Object> scenario = new DefaultScenario<>(machine, Mockito.mock(ScenarioPersister.class),
            Mockito.mock(ScenarioIdResolver.class));
        assertThat(machine.getStates()).hasSize(4);
        assertThat(machine.getTransitions()).hasSize(5);
        for (Transition<Object, ScenarioEvent> transition : machine.getTransitions()) {
            if (transition.getSource().getId().equals(StateEnum.INITIAL)) {
                assertThat(transition.getSource().getId()).isEqualTo(StateEnum.INITIAL);
                State<Object, ScenarioEvent> target = transition.getTarget();
                assertThat(target.getId()).isEqualTo("TELEGRAM_CHOICE");
                Set<UpdateRequestMappingAccessor> mappings = transition.getTrigger().getEvent().getMappings();
                assertThat(mappings).hasSize(1);
                assertThat(
                    mappings.contains(new UpdateRequestMapping("/telegramsettings", RequestType.MESSAGE,
                        Set.of(MessageType.COMMAND), false))).isTrue();
                DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
                    UpdateBuilder._default("/telegramsettings").command());
                request.setScenario(scenario);
                var result = scenario.sendEvent(request);
                if (result.exception() != null) {
                    throw result.exception();
                }
                assertThat(result.success()).isTrue();
                assertThat(executed).isTrue();
            }
        }
    }

    @TelegramScenario
    @Slf4j
    static class TestScenarioFactory {

        @TelegramScenarioAction(fullName = true)
        public void name(ActionContext<?> context) {
            executed = true;
        }

        @TelegramScenarioAction("test_name")
        public void name2(ActionContext<?> context) {
            executed = true;
        }

        @TelegramScenarioAction
        public void name3(ActionContext<?> context) {
            executed = true;
        }
    }

    enum StateEnum {
        INITIAL
    }
}