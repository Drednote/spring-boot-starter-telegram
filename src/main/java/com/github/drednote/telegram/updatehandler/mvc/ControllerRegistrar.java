package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.request.RequestMappingInfo;
import java.lang.reflect.Method;

@FunctionalInterface
public interface ControllerRegistrar {

  void register(Object bean, Method method, RequestMappingInfo info);
}
