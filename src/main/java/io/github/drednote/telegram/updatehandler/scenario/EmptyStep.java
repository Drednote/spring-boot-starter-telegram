package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyStep implements Step {

  public static final EmptyStep INSTANCE = new EmptyStep();

  @Override
  public Object onAction(TelegramUpdateRequest request) throws Exception {
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
