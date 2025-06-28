package io.github.drednote.telegram.datasource.scenario;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.drednote.telegram.datasource.kryo.AbstractKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.SimpleScenarioContext;
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
//            writeState(kryo, output, getMachine);
        }

        @Override
        public ScenarioContext<S> read(Kryo kryo, Input input, Class<ScenarioContext<S>> type) {
            String id = (String) kryo.readClassAndObject(input);
            StateMachineContext<S, ScenarioEvent> machineContext = machineContextSerializer.read(kryo, input, null);
            return new SimpleScenarioContext<>(id, machineContext);
        }

//        private void writeState(Kryo kryo, Output output, StateContext<S> getMachine) {
//            kryo.writeClassAndObject(output, getMachine.getId());
//            kryo.writeClassAndObject(output, getMachine.responseMessageProcessing());
//            var mappings = getMachine.updateRequestMappings();
//            kryo.writeClassAndObject(output, mappings.size());
//            mappings.forEach(mapping -> {
//                kryo.writeClassAndObject(output, mapping.getPattern());
//                kryo.writeClassAndObject(output, mapping.getRequestType());
//                kryo.writeClassAndObject(output, new HashSet<>(mapping.getMessageTypes()));
//            });
//            kryo.writeClassAndObject(output, new HashMap<>(getMachine.props()));
//        }
//
//        @SuppressWarnings("unchecked")
//        private @NonNull SimpleStateContext<S> readState(Kryo kryo, Input input) {
//            S getMachine = (S) kryo.readClassAndObject(input);
//            boolean responseMessageProcessing = (boolean) kryo.readClassAndObject(input);
//            Integer size = (Integer) kryo.readClassAndObject(input);
//            Set<UpdateRequestMappingAccessor> mappings = new HashSet<>();
//            for (int i = 0; i < size; i++) {
//                String pattern = (String) kryo.readClassAndObject(input);
//                RequestType requestType = (RequestType) kryo.readClassAndObject(input);
//                Set<MessageType> messageTypes = (Set<MessageType>) kryo.readClassAndObject(input);
//                mappings.add(new UpdateRequestMapping(pattern, requestType, messageTypes));
//            }
//            HashMap<String, Object> props = (HashMap<String, Object>) kryo.readClassAndObject(input);
//            return new SimpleStateContext<>(getMachine, mappings, responseMessageProcessing, props);
//        }
    }
}
