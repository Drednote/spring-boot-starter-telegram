package io.github.drednote.telegram.updatehandler.mvc;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import java.lang.reflect.Method;

@FunctionalInterface
public interface ControllerRegistrar {

  void register(Object bean, Method method, TelegramRequestMapping info);
}
