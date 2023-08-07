package io.github.drednote.telegram.datasource.kryo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ScenarioMachineSerializationServiceTest {

  ScenarioMachineSerializationService serializationService = new ScenarioMachineSerializationService();

  @Test
  void shouldCorrectSerializeAndDeserialize() throws IOException {
    ScenarioContext scenario = new ScenarioContext(1L, "rootName", "step", true);
    byte[] bytes = serializationService.serialize(scenario);
    ScenarioContext deserialize = serializationService.deserialize(bytes);
    assertThat(deserialize.getChatId()).isEqualTo(scenario.getChatId());
    assertThat(deserialize.getName()).isEqualTo(scenario.getName());
    assertThat(deserialize.isFinished()).isEqualTo(scenario.isFinished());
    assertThat(deserialize.getStepName()).isEqualTo(scenario.getStepName());
  }

  @Test
  void shouldNotPassValidation() {
    ScenarioContext scenario = new ScenarioContext(1L, null, "step", true);
    assertThatThrownBy(() -> serializationService.serialize(scenario)).isInstanceOf(
        IllegalArgumentException.class);

    ScenarioContext scenario2 = new ScenarioContext(1L, "step", null, true);
    assertThatThrownBy(() -> serializationService.serialize(scenario2)).isInstanceOf(
        IllegalArgumentException.class);

    ScenarioContext scenario3 = new ScenarioContext(null, "step", "s", true);
    assertThatThrownBy(() -> serializationService.serialize(scenario3)).isInstanceOf(
        IllegalArgumentException.class);
  }
}