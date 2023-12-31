package io.github.drednote.telegram.support;

import io.github.drednote.telegram.handler.scenario.ScenarioMonitor;
import io.github.drednote.telegram.handler.scenario.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestScenarioMonitor implements ScenarioMonitor {

  @Override
  public void madeStep(Step from, Step to) {
    log.info("Made step from {} to {}", from, to);
  }
}
