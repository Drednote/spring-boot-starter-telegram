package com.github.drednote.telegram.updatehandler.mvc;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ControllerRegistrar {

  void register(Object bean, Method method, BotRequestMappingInfo info);
}
