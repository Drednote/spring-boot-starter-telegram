package io.github.drednote.telegram.handler.scenario.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import io.github.drednote.telegram.handler.scenario.SimpleActionContext;
import io.github.drednote.telegram.handler.scenario.configurer.DefaultStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.StateConfigurer;
import io.github.drednote.telegram.handler.scenario.data.ScenarioState;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurerTest.TestScenarioFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
    void shouldCorrectCreateTransitionsFromProperties() throws Exception {
        assertThat(scenarioFactoryContainer.resolveAction(
            "io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurerTest$TestScenarioFactory#name(ActionContext)")).isNotNull();
        assertThat(scenarioFactoryContainer.resolveAction("test_name")).isNotNull();

        ScenarioBuilder<Object> scenarioBuilder = new ScenarioBuilder<>(StateMachineBuilder.builder());
        SimpleScenarioStateConfigurer<Object> scenarioStateConfigurer = new SimpleScenarioStateConfigurer<>(
            scenarioBuilder);
        StateConfigurer<Object> stateConfigurer = scenarioStateConfigurer.withStates();
        stateConfigurer.initial(StateEnum.INITIAL);

        ScenarioPropertiesConfigurer<Object> configurer = new ScenarioPropertiesConfigurer<>(scenarioBuilder, scenarioProperties,
            scenarioFactoryContainer);
        configurer.configure(scenarioStateConfigurer.withStates());

        ScenarioData<Object> build = scenarioBuilder.build();
        assertThat(build).isNotNull();
        StateMachine<Object, ScenarioEvent> machine = build.factory().getStateMachine();
        assertThat(machine.getStates()).hasSize(4);
        for (State<Object, ScenarioEvent> state : machine.getStates()) {
            if (state.getId().equals(StateEnum.INITIAL)) {
                Collection<State<Object, ScenarioEvent>> transition = state.getStates();
                assertThat(transition).hasSize(1);
//                assertThat(transition.getSource().getId()).isEqualTo(StateEnum.INITIAL);
//                ScenarioState<Object> target = transition.getTarget();
//                assertThat(target.getId()).isEqualTo("TELEGRAM_CHOICE");
//                assertThat(target.getMappings()).hasSize(1);
//                assertThat(target.getProps()).hasSize(2);
//                assertThat(
//                    target.getMappings().contains(new UpdateRequestMapping("/telegramsettings", RequestType.MESSAGE,
//                        Set.of(MessageType.COMMAND), false))).isTrue();
//                assertThatNoException().isThrownBy(() -> target.execute(new SimpleActionContext<>(
//                    Mockito.mock(UpdateRequest.class), Mockito.mock(Transition.class), new HashMap<>())));
//                assertThat(executed).isTrue();
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