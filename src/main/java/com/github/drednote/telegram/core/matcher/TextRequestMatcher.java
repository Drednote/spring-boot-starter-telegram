package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import com.github.drednote.telegram.core.request.TelegramRequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;

@RequiredArgsConstructor
public class TextRequestMatcher implements RequestMatcher {

  private final TelegramRequestMapping mapping;

  @Override
  public boolean matches(TelegramUpdateRequest request) {
    String text = request.getText();
    TelegramRequestMapping condition = mapping.getMatchingCondition(text);
    return condition != null;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }
}
