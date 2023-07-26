package com.github.drednote.telegram.updatehandler.scenario;

import org.springframework.lang.Nullable;

public interface Result {

  boolean made();

  @Nullable
  Object response();
}
