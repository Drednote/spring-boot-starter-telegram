package com.github.drednote.telegram.core;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(UpdateRequest updateRequest) throws Exception;
}
