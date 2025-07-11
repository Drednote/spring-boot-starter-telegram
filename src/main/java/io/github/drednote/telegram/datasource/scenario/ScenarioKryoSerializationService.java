package io.github.drednote.telegram.datasource.scenario;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.datasource.kryo.AbstractKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.DefaultScenarioContext;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.kryo.MessageHeadersSerializer;
import org.springframework.statemachine.kryo.StateMachineContextSerializer;
import org.springframework.statemachine.kryo.UUIDSerializer;

public class ScenarioKryoSerializationService<S> extends AbstractKryoSerializationService<ScenarioContext<S>> {

    private final StateMachineContextSerializer<S, ScenarioEvent> machineContextSerializer = new StateMachineContextSerializer<>();
    private final ScenarioContextSerializer<S> contextSerializer = new ScenarioContextSerializer<>(
        machineContextSerializer);
    private final ScenarioEventSerializer scenarioEventSerializer = new ScenarioEventSerializer();

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
        kryo.addDefaultSerializer(ScenarioEvent.class, scenarioEventSerializer);
        kryo.addDefaultSerializer(StateMachineContext.class, machineContextSerializer);
        kryo.addDefaultSerializer(MessageHeaders.class, new MessageHeadersSerializer());
        kryo.addDefaultSerializer(UUID.class, new UUIDSerializer());
    }

    private static class ScenarioContextSerializer<S> extends Serializer<ScenarioContext<S>> {

        private final StateMachineContextSerializer<S, ScenarioEvent> machineContextSerializer;

        public ScenarioContextSerializer(StateMachineContextSerializer<S, ScenarioEvent> machineContextSerializer) {
            this.machineContextSerializer = machineContextSerializer;
        }

        @Override
        public void write(Kryo kryo, Output output, ScenarioContext<S> object) {
            kryo.writeClassAndObject(output, object.getId());
            machineContextSerializer.write(kryo, output, object.getMachine());
        }

        @Override
        public ScenarioContext<S> read(Kryo kryo, Input input, Class<ScenarioContext<S>> type) {
            String id = (String) kryo.readClassAndObject(input);
            StateMachineContext<S, ScenarioEvent> machineContext = machineContextSerializer.read(kryo, input, null);
            return new DefaultScenarioContext<>(id, machineContext);
        }
    }

    private static class ScenarioEventSerializer extends Serializer<ScenarioEvent> {

        @Override
        public void write(Kryo kryo, Output output, ScenarioEvent object) {
            var mappings = object.getMappings();
            kryo.writeClassAndObject(output, mappings.size());
            mappings.forEach(mapping -> {
                kryo.writeClassAndObject(output, mapping.getPattern());
                kryo.writeClassAndObject(output, mapping.getRequestType());
                kryo.writeClassAndObject(output, new HashSet<>(mapping.getMessageTypes()));
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public ScenarioEvent read(Kryo kryo, Input input, Class<ScenarioEvent> type) {
            Integer size = (Integer) kryo.readClassAndObject(input);
            Set<UpdateRequestMapping> mappings = new HashSet<>();
            for (int i = 0; i < size; i++) {
                String pattern = (String) kryo.readClassAndObject(input);
                RequestType requestType = (RequestType) kryo.readClassAndObject(input);
                Set<MessageType> messageTypes = (Set<MessageType>) kryo.readClassAndObject(input);
                mappings.add(new UpdateRequestMapping(pattern, requestType, messageTypes));
            }
            return new ScenarioEvent(mappings);
        }
    }
}
