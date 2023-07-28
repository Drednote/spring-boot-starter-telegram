package com.github.drednote.telegram.datasource.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ScenarioMachineSerializationService extends
    AbstractKryoSerializationService<ScenarioContext> {

  private final ScenarioContextSerializer serializer = new ScenarioContextSerializer();

  @Override
  protected void doEncode(Kryo kryo, ScenarioContext object, Output output) {
    validate(object);
    kryo.writeObject(output, object, serializer);
  }

  private void validate(ScenarioContext object) {
    if (object.getChatId() == null) {
      throw new IllegalArgumentException("Cannot write 'scenario' to context. 'Id' cannot be null");
    }
    if (object.getName() == null) {
      throw new IllegalArgumentException(
          "Cannot write 'scenario' to context. 'Name' cannot be null");
    }
    if (object.getStepName() == null) {
      throw new IllegalArgumentException(
          "Cannot write 'scenario' to context. 'Step name' cannot be null");
    }
  }

  @Override
  protected ScenarioContext doDecode(Kryo kryo, Input input) {
    return kryo.readObject(input, ScenarioContext.class);
  }

  @Override
  protected void configureKryoInstance(Kryo kryo) {
    kryo.register(ScenarioContext.class, serializer);
  }

  private static class ScenarioContextSerializer extends Serializer<ScenarioContext> {

    @Override
    public void write(Kryo kryo, Output output, ScenarioContext object) {
      kryo.writeObject(output, object.getChatId());
      kryo.writeObject(output, object.getName());
      kryo.writeObject(output, object.isFinished());
      kryo.writeObject(output, object.getStepName());
    }

    @Override
    public ScenarioContext read(Kryo kryo, Input input, Class<ScenarioContext> type) {
      ScenarioContext scenario = new ScenarioContext();
      scenario.setChatId(kryo.readObject(input, Long.class));
      scenario.setName(kryo.readObject(input, String.class));
      scenario.setFinished(kryo.readObject(input, Boolean.class));
      scenario.setStepName(kryo.readObject(input, String.class));
      return scenario;
    }
  }
}
