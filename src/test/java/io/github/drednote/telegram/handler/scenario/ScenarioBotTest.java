package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.handler.UpdateHandler;
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
        new DefaultUpdateRequest(UpdateUtils.createCommand("/start"), null, null, null));
    updateHandler.onUpdate(
        new DefaultUpdateRequest(UpdateUtils.createCommand("/cancel"), null, null, null));

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