package io.github.drednote.telegram.handler.scenario.configurer;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioTransitionConfigurer.TransitionData;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScenarioBuilderTest {

    @Test
    public void testBuildStates() {
        // Создание мока для TelegramRequest
        TelegramRequest requestMock = Mockito.mock(TelegramRequest.class);

        // Создание списка действий
        List<Action> actions = Collections.singletonList(Mockito.mock(Action.class));

        // Создание TransitionData
        TransitionData<String> transition1 = new TransitionData<>("state1", "state2", actions, requestMock);
        TransitionData<String> transition2 = new TransitionData<>("state2", "state3", actions, requestMock);

        List<TransitionData<String>> transitionDataList = Arrays.asList(transition1, transition2);

        // Вызов тестируемого метода
        Map<State<String>, List<Transition<String>>> result = ScenarioBuilder.buildStates(new HashMap<>(), transitionDataList);

        // Проверка результатов
        assertEquals(2, result.size());


        SimpleState<String> state1 = new SimpleState<>("state1");
        SimpleState<String> state2 = new SimpleState<>("state2");
        SimpleState<String> state3 = new SimpleState<>("state3");

        // Проверка переходов из state1 в state2 и из state2 в state3
        assertEquals(1, result.get(state1).size());
        assertEquals(state2, result.get(state1).get(0).getTarget());

        assertEquals(1, result.get(state2).size());
        assertEquals(state3, result.get(state2).get(0).getTarget());

        assertThat(result.get(state3)).isNull();

        // Проверка наличия действий и маппингов

        assertThat(((SimpleState<String>) result.get(state1).get(0).getSource()).getMappings()).isNull();
        assertThat(((SimpleState<String>) result.get(state1).get(0).getTarget()).getMappings()).isNotNull();

        assertThat(((SimpleState<String>) result.get(state2).get(0).getSource()).getMappings()).isNotNull();
        assertThat(((SimpleState<String>) result.get(state2).get(0).getTarget()).getMappings()).isNotNull();

        assertThat(((SimpleState<String>) result.get(state1).get(0).getSource()).getActions()).isNull();
        assertThat(((SimpleState<String>) result.get(state1).get(0).getTarget()).getActions()).isNotNull();

        assertThat(((SimpleState<String>) result.get(state2).get(0).getSource()).getActions()).isNotNull();
        assertThat(((SimpleState<String>) result.get(state2).get(0).getTarget()).getActions()).isNotNull();
    }

    @Test
    public void testBuildStatesAmbiguousMapping() {
        // Создание мока для TelegramRequest
        TelegramRequest requestMock = Mockito.mock(TelegramRequest.class);

        // Создание списка действий
        List<Action> actions = Collections.singletonList(Mockito.mock(Action.class));

        // Создание TransitionData с одинаковым source
        TransitionData<String> transition1 = new TransitionData<>("state1", "state2", actions, requestMock);
        TransitionData<String> transition2 = new TransitionData<>("state1", "state3", actions, requestMock);

        List<TransitionData<String>> transitionDataList = Arrays.asList(transition1, transition2);

        // Ожидаемое исключение IllegalStateException из-за двусмысленного маппинга
        assertThrows(IllegalStateException.class, () -> {
            Map<State<String>, List<Transition<String>>> result = ScenarioBuilder.buildStates(new HashMap<>(), transitionDataList);
            System.out.println("result = " + result);
        });
    }
}