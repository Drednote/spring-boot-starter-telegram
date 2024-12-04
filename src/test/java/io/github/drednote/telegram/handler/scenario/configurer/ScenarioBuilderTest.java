package io.github.drednote.telegram.handler.scenario.configurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.TelegramRequests;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScenarioBuilderTest {

    @Test
    public void testBuildStates() {
        // Создание мока для TelegramRequest
        TelegramRequest requestMock = Mockito.mock(TelegramRequest.class);

        // Создание списка действий
        List<Action<String>> actions = Collections.singletonList(Mockito.mock(Action.class));

        // Создание TransitionData
        TransitionData<String> transition1 = new TransitionData<>("state1", "state2", actions, requestMock, new HashMap<>());
        TransitionData<String> transition2 = new TransitionData<>("state2", "state3", actions, requestMock, new HashMap<>());

        List<TransitionData<String>> transitionDataList = Arrays.asList(transition1, transition2);

        // Вызов тестируемого метода
        var result = ScenarioBuilder.buildStates(transitionDataList);

        // Проверка результатов
        assertEquals(2, result.size());

        UpdateRequestMappingBuilder builder = new UpdateRequestMappingBuilder(requestMock);
        Set<UpdateRequestMapping> mappings = new HashSet<>();
        builder.forEach(mappings::add);
        SimpleState<String> state1 = new SimpleState<>("state1");
        SimpleState<String> state2Null = new SimpleState<>("state2");
        SimpleState<String> state2 = new SimpleState<>("state2", mappings, new HashMap<>());
        SimpleState<String> state3 = new SimpleState<>("state3", mappings, new HashMap<>());

        // Проверка переходов из state1 в state2 и из state2 в state3
        assertEquals(1, result.get(state1).size());
        assertEquals(state2, result.get(state1).get(0).getTarget());

        assertEquals(1, result.get(state2Null).size());
        assertEquals(state3, result.get(state2Null).get(0).getTarget());

        assertThat(result.get(state3)).isNull();

        // Проверка наличия действий и маппингов

        assertThat(result.get(state1).get(0).getSource().getMappings()).isEmpty();
        assertThat(result.get(state1).get(0).getTarget().getMappings()).isNotEmpty();

        assertThat(result.get(state2Null).get(0).getSource().getMappings()).isEmpty();
        assertThat(result.get(state2Null).get(0).getTarget().getMappings()).isNotEmpty();

        assertThat(((SimpleState<String>) result.get(state1).get(0).getSource()).getActions()).isNull();
        assertThat(((SimpleState<String>) result.get(state1).get(0).getTarget()).getActions()).isNotNull();

        assertThat(((SimpleState<String>) result.get(state2Null).get(0).getSource()).getActions()).isNull();
        assertThat(((SimpleState<String>) result.get(state2Null).get(0).getTarget()).getActions()).isNotNull();
    }

    @Test
    public void testBuildStatesAmbiguousMapping() {
        // Создание мока для TelegramRequest
        TelegramRequest requestMock = Mockito.mock(TelegramRequest.class);

        // Создание списка действий
        List<Action<String>> actions = Collections.singletonList(Mockito.mock(Action.class));

        // Создание TransitionData с одинаковым source
        TransitionData<String> transition1 = new TransitionData<>("state1", "state2", actions, requestMock, new HashMap<>());
        TransitionData<String> transition2 = new TransitionData<>("state1", "state2", actions, requestMock, new HashMap<>());

        List<TransitionData<String>> transitionDataList = Arrays.asList(transition1, transition2);

        // Ожидаемое исключение IllegalStateException из-за двусмысленного маппинга
        assertThrows(IllegalStateException.class, () -> {
            ScenarioBuilder.buildStates(transitionDataList);
        });
    }

    @Test
    void shouldNotOverrideMappings() {
        // Создание мока для TelegramRequest
        TelegramRequest requestMock = Mockito.mock(TelegramRequest.class);
        TelegramRequest requestMock2 = TelegramRequests.text("Hello");

        // Создание списка действий
        List<Action<String>> actions = Collections.singletonList(Mockito.mock(Action.class));

        // Создание TransitionData
        TransitionData<String> transition1 = new TransitionData<>("state1", "state3", actions, requestMock, new HashMap<>());
        TransitionData<String> transition2 = new TransitionData<>("state2", "state3", actions, requestMock2, new HashMap<>());

        List<TransitionData<String>> transitionDataList = Arrays.asList(transition1, transition2);

        // Вызов тестируемого метода
        var result = ScenarioBuilder.buildStates(transitionDataList);

        Set<UpdateRequestMapping> mappings1 = new UpdateRequestMappingBuilder(requestMock).build();

        Set<UpdateRequestMapping> mappings2 = new UpdateRequestMappingBuilder(requestMock2).build();

        List<Transition<String>> transitions1 = result.get(new SimpleState<>("state1"));
        List<Transition<String>> transitions2 = result.get(new SimpleState<>("state2"));

        assertThat(transitions1).hasSize(1);
        assertThat(transitions1.get(0).getTarget()).isEqualTo(new SimpleState<>("state3", mappings1, new HashMap<>()));

        assertThat(transitions2).hasSize(1);
        assertThat(transitions2.get(0).getTarget()).isEqualTo(new SimpleState<>("state3", mappings2, new HashMap<>()));
    }
}