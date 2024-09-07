package io.github.drednote.telegram.handler.scenario_deprecated;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.handler.UpdateHandlerProperties;
import io.github.drednote.telegram.handler.scenario_deprecated.configurer.ScenarioMachineConfigurerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScenarioMachineContainerTest {

  ScenarioMachineContainer container;

  @BeforeEach
  void setUp() {
    ScenarioMachineConfigurerImpl configurer = new ScenarioMachineConfigurerImpl();
    container = new ScenarioMachineContainer(configurer, new UpdateHandlerProperties());
  }

  @Test
  void shouldReturnInMemoryDefaultPersister() {
    assertThat(container.getScenarioPersister()).isNull();
  }
}