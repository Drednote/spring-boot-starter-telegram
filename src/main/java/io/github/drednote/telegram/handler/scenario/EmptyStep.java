package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@BetaApi
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyStep implements Step {

  public static final EmptyStep INSTANCE = new EmptyStep();

  @Override
  public Object onAction(UpdateRequest request) throws Exception {
    throw new UnsupportedOperationException("Cannot execute action on empty step");
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Scenario getRoot() {
    return null;
  }
}
