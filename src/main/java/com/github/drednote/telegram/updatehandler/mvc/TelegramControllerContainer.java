package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import com.github.drednote.telegram.core.request.TelegramRequestMapping;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.method.HandlerMethod;

public class TelegramControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<TelegramRequestMapping, HandlerMethod> mappingLookup = new HashMap<>();

  @Override
  public void populate(ExtendedTelegramUpdateRequest request) {
    String text = request.getText() == null ? "" : request.getText();

    mappingLookup.keySet().stream()
        .filter(requestMappingInfo -> requestMappingInfo.matches(request))
        .min(TelegramRequestMapping::compareTo)
        .ifPresent(mappingInfo -> {
          String pattern = mappingInfo.getPattern();
          Map<String, String> templateVariables =
              mappingInfo.getPathMatcher().extractUriTemplateVariables(pattern, text);
          request.setRequestHandler(
              new RequestHandler(mappingLookup.get(mappingInfo), templateVariables, pattern));
        });
  }

  @Override
  public void register(Object bean, Method method, TelegramRequestMapping mapping) {
    HandlerMethod handlerMethod = new HandlerMethod(bean, method);
    HandlerMethod existingHandler = mappingLookup.get(mapping);
    if (existingHandler != null) {
      throw new IllegalStateException(
          "\nAmbiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" +
              handlerMethod + "\nto " + mapping + ": There is already '" +
              existingHandler.getBean() + "' bean method\n" + existingHandler + " mapped.");
    }
    mappingLookup.put(mapping, handlerMethod);
  }
}
