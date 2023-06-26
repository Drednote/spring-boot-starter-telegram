package com.github.drednote.telegrambot.session.backoff;

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
