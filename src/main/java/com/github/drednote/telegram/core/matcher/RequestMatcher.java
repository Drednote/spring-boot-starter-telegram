package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.request.BotRequest;

public interface RequestMatcher {

  boolean matches(BotRequest request);
}
