package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.ExtendedBotRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(ExtendedBotRequest descriptor);
}
