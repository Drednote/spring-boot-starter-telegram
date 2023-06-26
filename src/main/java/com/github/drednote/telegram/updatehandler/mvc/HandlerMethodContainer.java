package com.github.drednote.telegram.updatehandler.mvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HandlerMethodContainer implements HandlerMethodLookup, ControllerRegistrar {

  private final Map<BotRequestMappingInfo, HandlerMethod> mappingLookup = new HashMap<>();

  @Nullable
  public HandlerMethod lookup(Update update) {
    for (BotRequestMappingInfo requestMappingInfo : mappingLookup.keySet()) {
      String text = update.getMessage().getText();
      BotRequestMappingInfo matchingCondition = requestMappingInfo.getMatchingCondition(text);
      if (matchingCondition != null) {
        Set<String> patterns = matchingCondition.getPatterns();
        if (!patterns.isEmpty()) {
          String basePattern = patterns.iterator().next();
          Map<String, String> templateVariables =
              matchingCondition.getPathMatcher().extractUriTemplateVariables(basePattern, text);
        }
        return mappingLookup.get(requestMappingInfo);
      }
    }
    return null;
  }

  public void register(Object bean, Method invocableMethod, BotRequestMappingInfo info) {
    HandlerMethod handlerMethod = new HandlerMethod(bean, invocableMethod);
    mappingLookup.put(info, handlerMethod);
  }
}
