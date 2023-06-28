package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;
import org.springframework.lang.Nullable;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(UpdateRequest mvcUpdateRequest);
}
