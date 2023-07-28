package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.testsupport.UpdateUtils;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Disabled
//@SpringBootTest(classes = Config.class)
class ScenarioBotTest {

  @Qualifier("scenarioUpdateHandler")
  @Autowired
  UpdateHandler updateHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void name() throws Exception {
    updateHandler.onUpdate(
        new UpdateRequest(UpdateUtils.createCommand("/start"), null, null));
    updateHandler.onUpdate(
        new UpdateRequest(UpdateUtils.createCommand("/cancel"), null, null));

  }

//  @Configuration
//  @ComponentScan(basePackageClasses = Scenario.class)
//  @Import(UpdateHandlerAutoConfiguration.class)
//  public static class Config implements ScenarioAdapter {
//
//    @Override
//    public void onConfigure(ScenarioConfigurer configurer) {
//      configurer.addScenario(new ScenarioBuilder("/start")
//          .step()
//          .withActionExecutor(updateRequest -> {
//            log.info("first -> " + updateRequest.getChatId());
//            return null;
//          })
//          .next()
//
//          .step()
//          .withActionExecutor(updateRequest -> {
//            log.info("last -> " + updateRequest.getChatId());
//            return null;
//          })
//          .next().build());
//    }
//  }
}