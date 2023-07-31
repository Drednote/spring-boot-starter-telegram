package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(ExtendedBotRequest descriptor);
}
