package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.method.HandlerMethod;

public class BotControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<BotRequestMappingInfo, HandlerMethod> mappingLookup = new HashMap<>();
  private final CompositeMappingInfoComparator comparator = new CompositeMappingInfoComparator();

  @Override
  public void populate(UpdateRequest updateRequest) {
    List<BotRequestMappingInfo> mappings = new ArrayList<>(1);
    String text = updateRequest.getText() == null ? "" : updateRequest.getText();
    for (BotRequestMappingInfo requestMappingInfo : mappingLookup.keySet()) {
      if (requestMappingInfo.getType() != null
          && requestMappingInfo.getType() != updateRequest.getMessageType()) {
        continue;
      }
      BotRequestMappingInfo matchingCondition = requestMappingInfo.getMatchingCondition(text);
      if (matchingCondition != null) {
        mappings.add(matchingCondition);
      }
    }
    mappings.stream()
        .min(comparator)
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
