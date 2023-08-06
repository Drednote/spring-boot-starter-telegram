package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(ExtendedTelegramUpdateRequest descriptor);
}
