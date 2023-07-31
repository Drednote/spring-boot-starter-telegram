package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.BotRequest;
import com.github.drednote.telegram.core.ExtendedBotRequest;

public abstract class DefaultUpdateFilter implements UpdateFilter {

  abstract void doFilter(ExtendedBotRequest request) throws Exception;

  @Override
  public void filter(BotRequest request) throws Exception {
    if (request instanceof ExtendedBotRequest extendedBotRequest) {
      doFilter(extendedBotRequest);
    }
  }
}
