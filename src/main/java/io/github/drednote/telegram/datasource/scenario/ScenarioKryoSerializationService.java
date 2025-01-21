package io.github.drednote.telegram.datasource.scenario;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.datasource.kryo.AbstractKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleStateContext;
import io.github.drednote.telegram.handler.scenario.persist.StateContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.springframework.lang.NonNull;

public class ScenarioKryoSerializationService<S> extends AbstractKryoSerializationService<ScenarioContext<S>> {

    private final ScenarioContextSerializer<S> contextSerializer = new ScenarioContextSerializer<>();

    @Override
    protected void doEncode(Kryo kryo, ScenarioContext<S> object, Output output) {
        kryo.writeObject(output, object, contextSerializer);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ScenarioContext<S> doDecode(Kryo kryo, Input input) {
        return kryo.readObject(input, ScenarioContext.class);
    }

    @Override
    protected void configureKryoInstance(Kryo kryo) {
        kryo.addDefaultSerializer(ScenarioContext.class, contextSerializer);
    }

    private static class ScenarioContextSerializer<S> extends Serializer<ScenarioContext<S>> {

        @Override
        public void write(Kryo kryo, Output output, ScenarioContext<S> object) {
            StateContext<S> state = object.state();
            kryo.writeClassAndObject(output, object.id());
            writeState(kryo, output, state);
        }

        private void writeState(Kryo kryo, Output output, StateContext<S> state) {
            kryo.writeClassAndObject(output, state.id());
            kryo.writeClassAndObject(output, state.responseMessageProcessing());
            var mappings = state.updateRequestMappings();
            kryo.writeClassAndObject(output, mappings.size());
            mappings.forEach(mapping -> {
                kryo.writeClassAndObject(output, mapping.getPattern());
                kryo.writeClassAndObject(output, mapping.getRequestType());
                kryo.writeClassAndObject(output, new HashSet<>(mapping.getMessageTypes()));
            });
            kryo.writeClassAndObject(output, new HashMap<>(state.props()));
        }

        @Override
        public ScenarioContext<S> read(Kryo kryo, Input input, Class<ScenarioContext<S>> type) {
            String id = (String) kryo.readClassAndObject(input);
            SimpleStateContext<S> stateContext = readState(kryo, input);
            return new SimpleScenarioContext<>(id, stateContext);
        }

        @SuppressWarnings("unchecked")
        private @NonNull SimpleStateContext<S> readState(Kryo kryo, Input input) {
            S state = (S) kryo.readClassAndObject(input);
            boolean responseMessageProcessing = (boolean) kryo.readClassAndObject(input);
            Integer size = (Integer) kryo.readClassAndObject(input);
            Set<UpdateRequestMappingAccessor> mappings = new HashSet<>();
            for (int i = 0; i < size; i++) {
                String pattern = (String) kryo.readClassAndObject(input);
                RequestType requestType = (RequestType) kryo.readClassAndObject(input);
                Set<MessageType> messageTypes = (Set<MessageType>) kryo.readClassAndObject(input);
                mappings.add(new UpdateRequestMapping(pattern, requestType, messageTypes));
            }
            HashMap<String, Object> props = (HashMap<String, Object>) kryo.readClassAndObject(input);
            return new SimpleStateContext<>(state, mappings, responseMessageProcessing, props);
        }
    }
}
