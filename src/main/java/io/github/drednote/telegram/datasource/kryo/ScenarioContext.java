package io.github.drednote.telegram.datasource.kryo;

import io.github.drednote.telegram.handler.scenario.Scenario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioContext {

  private Long chatId;

  private String name;
  private String stepName;
  private boolean finished;

  public static ScenarioContext from(Scenario scenario) {
    return new ScenarioContext(scenario.getId(), scenario.getName(),
        scenario.getCurrentStep().getName(),
        scenario.isFinished());
  }
}
