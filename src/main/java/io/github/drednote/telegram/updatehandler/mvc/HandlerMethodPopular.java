package io.github.drednote.telegram.updatehandler.mvc;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(ExtendedTelegramUpdateRequest descriptor);
}
