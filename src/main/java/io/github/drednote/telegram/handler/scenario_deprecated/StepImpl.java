package io.github.drednote.telegram.handler.scenario_deprecated;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@BetaApi
@RequiredArgsConstructor
public class StepImpl implements Step {

  @NonNull
  final Scenario root;
  @NonNull
  final ActionExecutor actionExecutor;
  @NonNull
  final String name;

  @Override
  public Object onAction(UpdateRequest request) throws Exception {
    return actionExecutor.onAction(request);
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public Scenario getRoot() {
    return root;
  }

  @Override
  public String toString() {
    return "'%s'".formatted(name);
  }
}
