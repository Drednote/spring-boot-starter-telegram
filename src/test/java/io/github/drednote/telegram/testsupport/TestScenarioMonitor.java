package io.github.drednote.telegram.testsupport;

import io.github.drednote.telegram.updatehandler.scenario.ScenarioMonitor;
import io.github.drednote.telegram.updatehandler.scenario.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestScenarioMonitor implements ScenarioMonitor {

  @Override
  public void madeStep(Step from, Step to) {
    log.info("Made step from {} to {}", from, to);
  }
}
