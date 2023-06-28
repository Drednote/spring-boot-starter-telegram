package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.web.method.HandlerMethod;

public class BotControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<BotRequestMappingInfo, HandlerMethod> mappingLookup = new HashMap<>();

  @Override
  public void populate(UpdateRequest updateRequest) {
    for (Entry<BotRequestMappingInfo, HandlerMethod> entry : mappingLookup.entrySet()) {
      BotRequestMappingInfo requestMappingInfo = entry.getKey();
      HandlerMethod handlerMethod = entry.getValue();
      if (requestMappingInfo.getType() != updateRequest.getMessageType()) {
        continue;
      }
      String text = updateRequest.getText();
      if (text != null) {
        BotRequestMappingInfo matchingCondition = requestMappingInfo.getMatchingCondition(text);
        if (matchingCondition != null) {
          String pattern = matchingCondition.getPattern();
          Map<String, String> templateVariables =
              matchingCondition.getPathMatcher().extractUriTemplateVariables(pattern, text);
          updateRequest.setBasePattern(pattern);
          updateRequest.setTemplateVariables(templateVariables);
          updateRequest.setHandlerMethod(handlerMethod);
        }
      }
    }
  }

  @Override
  public void register(Object bean, Method method, BotRequestMappingInfo mapping) {
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
