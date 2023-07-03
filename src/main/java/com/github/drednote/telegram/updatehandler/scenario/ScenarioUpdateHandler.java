package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.ImmutableUpdateRequest;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import com.github.drednote.telegram.updatehandler.scenario.Scenario.Step;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
public class ScenarioUpdateHandler implements UpdateHandler {

  private final Collection<Scenario> scenarios;

  @Override
  public void onUpdate(UpdateRequest request) {
    if (request.getUser() != null) { // because need status of current user
      for (Scenario scenario : scenarios) {
        if (scenario.isMatch(request)) {
          doHandle(scenario, request);
        }
        if (scenario.isCancel(request)) {
          doCancel(scenario, request);
        }
      }
    }
    if (request.getResponse() == null) {
      request.setResponse(new NotHandledHandlerResponse(request.getOrigin()));
    }
  }

  private void doHandle(Scenario scenario, UpdateRequest request) {
    List<Step> steps = scenario.getSteps();
    if (!CollectionUtils.isEmpty(steps)) {
      log.info("Execute 0 scenario for {}", scenario);
      Object apply = steps.get(0).getRequest().apply(new ImmutableUpdateRequest(request));
      setResponse(request, apply);
    }
  }

  private void doCancel(Scenario scenario, UpdateRequest request) {
    Object apply = scenario.getCancel().getAction().apply(new ImmutableUpdateRequest(request));
    setResponse(request, apply);
  }
}
