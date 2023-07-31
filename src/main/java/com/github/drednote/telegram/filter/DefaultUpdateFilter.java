package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.BotRequest;
import com.github.drednote.telegram.core.request.ExtendedBotRequest;

public abstract class DefaultUpdateFilter implements UpdateFilter {

  abstract void doFilter(ExtendedBotRequest request) throws Exception;

  @Override
  public void filter(BotRequest request) throws Exception {
    if (request instanceof ExtendedBotRequest extendedBotRequest) {
      doFilter(extendedBotRequest);
    }
  }
}
