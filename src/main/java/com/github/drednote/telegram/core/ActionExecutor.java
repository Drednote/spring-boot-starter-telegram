package com.github.drednote.telegram.core;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(BotRequest request) throws Exception;
}
