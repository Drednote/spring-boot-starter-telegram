package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.RequestType;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.core.RequestMappingInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TextRequestMatcher implements RequestMatcher {

  private final RequestMappingInfo mapping;

  @Override
  public boolean matches(UpdateRequest updateRequest) {
    String text = updateRequest.getText();
    RequestType messageType = updateRequest.getMessageType();
    if (mapping.getType() != null && messageType != mapping.getType()) {
      return false;
    }
    RequestMappingInfo condition = mapping.getMatchingCondition(text);
    return condition != null;
  }
}
