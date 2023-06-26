package com.github.drednote.telegram.session;

import org.telegram.telegrambots.meta.generics.BackOff;

public class FixedBackoff implements BackOff {

  @Override
  public void reset() {
    // do nothing
  }

  @Override
  public long nextBackOffMillis() {
    return 500;
  }
}
