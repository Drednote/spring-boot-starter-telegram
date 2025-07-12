package io.github.drednote.telegram.datasource.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.TelegramRequests;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.DefaultScenarioContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;

class ScenarioKryoSerializationServiceTest {

    ScenarioKryoSerializationService<AbstractStateClass<?>> serializationService = new ScenarioKryoSerializationService<>();

    @Test
    void shouldCorrectSerializeAndDeserialize() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("id", BigDecimal.ONE);
        DefaultStateMachineContext<AbstractStateClass<?>, ScenarioEvent> target = new DefaultStateMachineContext<>(
            new StateClass(State.SOURCE), new ScenarioEvent(TelegramRequests.text("1")), map,
            new DefaultExtendedState());
        ScenarioContext<AbstractStateClass<?>> scenario = new DefaultScenarioContext<>("id", target);

        byte[] bytes = serializationService.serialize(scenario);
        ScenarioContext<AbstractStateClass<?>> deserialize = serializationService.deserialize(bytes);

        assertThat(deserialize).isEqualTo(scenario);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    static class StateClass extends AbstractStateClass<State> {

        private State state;
    }

    abstract static class AbstractStateClass<T> {

        public abstract T getState();

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AbstractStateClass<?> that = (AbstractStateClass<?>) o;
            return Objects.equals(getState(), that.getState());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getState());
        }
    }

    enum State {
        SOURCE, TARGET
    }

}