package io.github.drednote.telegram.datasource.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleStateContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleTransitionContext;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

class ScenarioKryoSerializationServiceTest {

    ScenarioKryoSerializationService<AbstractStateClass<?>> serializationService = new ScenarioKryoSerializationService<>();

    @Test
    void shouldCorrectSerializeAndDeserialize() throws IOException {
        SimpleStateContext<AbstractStateClass<?>> source = new SimpleStateContext<>(new StateClass(State.SOURCE));
        SimpleStateContext<AbstractStateClass<?>> target = new SimpleStateContext<>(new StateClass(State.TARGET));
        ScenarioContext<AbstractStateClass<?>> scenario = new SimpleScenarioContext<>("id", target,
            List.of(new SimpleTransitionContext<>(source, target)));

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