package io.github.drednote.telegram.datasource.scenario;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.drednote.telegram.datasource.kryo.AbstractKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleStateContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleTransitionContext;
import io.github.drednote.telegram.handler.scenario.persist.StateContext;
import io.github.drednote.telegram.handler.scenario.persist.TransitionContext;
import java.util.ArrayList;
import java.util.List;

public class ScenarioKryoSerializationService<S> extends AbstractKryoSerializationService<ScenarioContext<S>> {

    private final ScenarioContextSerializer contextSerializer = new ScenarioContextSerializer();
    private final TransitionSerializer transitionSerializer = new TransitionSerializer();
    private final StateSerializer stateSerializer = new StateSerializer();
    private final ListTransitionContextSerializer listTransitionContextSerializer = new ListTransitionContextSerializer();

    @Override
    protected void doEncode(Kryo kryo, ScenarioContext<S> object, Output output) {
        kryo.writeObject(output, object, contextSerializer);
    }

    @Override
    protected ScenarioContext<S> doDecode(Kryo kryo, Input input) {
        return kryo.readObject(input, ScenarioContext.class);
    }

    @Override
    protected void configureKryoInstance(Kryo kryo) {
        kryo.register(ScenarioContext.class, contextSerializer);
        kryo.register(TransitionContext.class, transitionSerializer);
        kryo.register(StateContext.class, stateSerializer);
    }

    private class ScenarioContextSerializer extends Serializer<ScenarioContext<S>> {


        @Override
        public void write(Kryo kryo, Output output, ScenarioContext object) {
            kryo.writeObject(output, object.getId());
            kryo.writeObject(output, object.getState(), stateSerializer);
            kryo.writeObject(output, object.getTransitionsHistory(), listTransitionContextSerializer);
        }

        @Override
        public ScenarioContext<S> read(Kryo kryo, Input input, Class<ScenarioContext<S>> type) {
            String id = kryo.readObject(input, String.class);
            StateContext<S> state = kryo.readObject(input, StateContext.class);
            List<TransitionContext<S>> transitions = kryo.readObject(
                input, ArrayList.class, listTransitionContextSerializer);
            return new SimpleScenarioContext<>(id, state, transitions);
        }
    }

    private class ListTransitionContextSerializer extends Serializer<List<TransitionContext<S>>> {

        @Override
        public void write(Kryo kryo, Output output, List<TransitionContext<S>> object) {
            kryo.writeObject(output, object.size());
            object.forEach(context -> kryo.writeObject(output, context, transitionSerializer));
        }

        @Override
        public List<TransitionContext<S>> read(Kryo kryo, Input input, Class<List<TransitionContext<S>>> type) {
            Integer count = kryo.readObject(input, Integer.class);
            List<TransitionContext<S>> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                result.add(kryo.readObject(input, TransitionContext.class));
            }
            return result;
        }
    }

    private class TransitionSerializer extends Serializer<TransitionContext<S>> {

        @Override
        public void write(Kryo kryo, Output output, TransitionContext<S> object) {
            kryo.writeObject(output, object.getSourceContext(), stateSerializer);
            kryo.writeObject(output, object.getTargetContext(), stateSerializer);
        }

        @Override
        public TransitionContext<S> read(Kryo kryo, Input input, Class<TransitionContext<S>> type) {
            StateContext<S> source = kryo.readObject(input, StateContext.class);
            StateContext<S> target = kryo.readObject(input, StateContext.class);
            return new SimpleTransitionContext<>(source, target);
        }
    }

    private class StateSerializer extends Serializer<StateContext<S>> {

        @Override
        public void write(Kryo kryo, Output output, StateContext object) {
            kryo.writeClassAndObject(output, object.getId());
        }

        @Override
        public StateContext<S> read(Kryo kryo, Input input, Class<StateContext<S>> type) {
            S name = (S) kryo.readClassAndObject(input);
            return new SimpleStateContext<>(name);
        }

    }
}
