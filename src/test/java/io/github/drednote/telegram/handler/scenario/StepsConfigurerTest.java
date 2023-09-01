package io.github.drednote.telegram.handler.scenario;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.handler.scenario.ActionExecutor;
import io.github.drednote.telegram.handler.scenario.ScenarioAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioNodeBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioDefinition;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioMachineConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioMachineConfigurerImpl;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class StepsConfigurerTest {

  ScenarioMachineConfigurerImpl configurer;

  @BeforeEach
  void setUp() {
    configurer = new ScenarioMachineConfigurerImpl();
    new Config().onConfigure(configurer);
  }

  @Test
  void name() throws JsonProcessingException {
    LinkedList<ScenarioDefinition> scenarios = configurer.getScenarios();
    String value = new ObjectMapper().writeValueAsString(scenarios);
    System.out.println("node = " + value);
    ScenarioNodeBuilder container = new ScenarioNodeBuilder(configurer.getScenarios());
    System.out.println("nodes = " + new ObjectMapper().writeValueAsString(container.getScenarios()));
  }

  public static class Config implements ScenarioAdapter {

    @Override
    public void onConfigure(ScenarioMachineConfigurer machineConfigurer) {
      ActionExecutor actionExecutor = r -> null;
      machineConfigurer
//          .withPersister(new MockPersister())

          .scenario()
          .startCommand("/register").action(actionExecutor)

          .step()
          .name("r1").action(actionExecutor)
          .child(s1 -> s1
              .name("r1_s1").action(actionExecutor)
              .child(s2 -> s2
                  .action(actionExecutor).next()
              )
              .next()
              .name("r1_cancel").pattern(new TelegramRequestMapping("Cancel", null, null))
              .action(actionExecutor).refToStep("r1").next()
          )
          .next()
          .name("r2").action(actionExecutor)
          .and()
      ;
    }

  }
}