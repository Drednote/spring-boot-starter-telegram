package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.request.BotRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyStep implements Step {

  public static final EmptyStep INSTANCE = new EmptyStep();

  @Override
  public Object onAction(BotRequest request) throws Exception {
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
