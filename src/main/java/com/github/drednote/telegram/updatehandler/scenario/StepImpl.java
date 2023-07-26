package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.ActionExecutor;
import com.github.drednote.telegram.core.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class StepImpl implements Step {

  @NonNull
  private final Scenario root;
  @NonNull
  private final ActionExecutor actionExecutor;
  @NonNull
  private final String name;

  @Override
  public Object onAction(UpdateRequest updateRequest) throws Exception {
    return actionExecutor.onAction(updateRequest);
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
}
