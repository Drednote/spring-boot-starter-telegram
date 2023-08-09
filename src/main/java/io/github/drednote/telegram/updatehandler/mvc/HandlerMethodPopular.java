package io.github.drednote.telegram.updatehandler.mvc;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(TelegramUpdateRequest descriptor);
}
