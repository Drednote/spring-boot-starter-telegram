package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;

@FunctionalInterface
public interface HandlerMethodPopular {

  void populate(UpdateRequest descriptor);
}
