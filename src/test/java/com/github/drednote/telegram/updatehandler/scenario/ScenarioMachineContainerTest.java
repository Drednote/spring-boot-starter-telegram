package com.github.drednote.telegram.updatehandler.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.updatehandler.UpdateHandlerProperties;
import com.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurerImpl;
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
    assertThat(container.getScenarioPersister()).isInstanceOf(InMemoryScenarioPersister.class);
  }
}