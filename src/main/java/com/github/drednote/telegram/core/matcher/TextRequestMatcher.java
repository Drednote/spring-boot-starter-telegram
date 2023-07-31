package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.request.BotRequest;
import com.github.drednote.telegram.core.request.RequestMappingInfo;
import com.github.drednote.telegram.core.request.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TextRequestMatcher implements RequestMatcher {

  private final RequestMappingInfo mapping;

  @Override
  public boolean matches(BotRequest request) {
    String text = request.getText();
    RequestType messageType = request.getMessageType();
    if (mapping.getType() != null && messageType != mapping.getType()) {
      return false;
    }
    RequestMappingInfo condition = mapping.getMatchingCondition(text);
    return condition != null;
  }
}
