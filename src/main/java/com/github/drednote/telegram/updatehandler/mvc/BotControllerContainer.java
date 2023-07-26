package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.RequestMappingInfo;
import com.github.drednote.telegram.core.UpdateRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.method.HandlerMethod;

public class BotControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<RequestMappingInfo, HandlerMethod> mappingLookup = new HashMap<>();

  @Override
  public void populate(UpdateRequest updateRequest) {
    String text = updateRequest.getText() == null ? "" : updateRequest.getText();

    mappingLookup.keySet().stream()
        .filter(requestMappingInfo -> requestMappingInfo.matches(updateRequest))
        .min(RequestMappingInfo::compareTo)
        .ifPresent(mappingInfo -> {
          String pattern = mappingInfo.getPattern();
          Map<String, String> templateVariables =
              mappingInfo.getPathMatcher().extractUriTemplateVariables(pattern, text);
          updateRequest.setBasePattern(pattern);
          updateRequest.setTemplateVariables(templateVariables);
          updateRequest.setHandlerMethod(mappingLookup.get(mappingInfo));
        });
  }

  @Override
  public void register(Object bean, Method method, RequestMappingInfo mapping) {
    HandlerMethod handlerMethod = new HandlerMethod(bean, method);
    HandlerMethod existingHandler = mappingLookup.get(mapping);
    if (existingHandler != null) {
      throw new IllegalStateException(
          "Ambiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" +
              handlerMethod + "\nto " + mapping + ": There is already '" +
              existingHandler.getBean() + "' bean method\n" + existingHandler + " mapped.");
    }
    mappingLookup.put(mapping, handlerMethod);
  }
}
