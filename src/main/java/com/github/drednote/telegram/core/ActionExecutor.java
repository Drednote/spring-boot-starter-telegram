package com.github.drednote.telegram.core;

import com.github.drednote.telegram.core.request.BotRequest;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(BotRequest request) throws Exception;
}
